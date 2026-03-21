package dev.jordanjoseph.backend.service;

import dev.jordanjoseph.backend.dto.account.AccountView;
import dev.jordanjoseph.backend.dto.transfer.InternalTransferRequest;
import dev.jordanjoseph.backend.dto.transfer.InternalTransferResponse;

import dev.jordanjoseph.backend.dto.transfer.OutgoingExternalTransferRequest;
import dev.jordanjoseph.backend.dto.transfer.event.TransferInitiatedEvent;
import dev.jordanjoseph.backend.exception.DuplicateTransactionException;
import dev.jordanjoseph.backend.model.*;
import dev.jordanjoseph.backend.repository.*;
import dev.jordanjoseph.backend.util.AccountGuard;
import dev.jordanjoseph.backend.util.InstantToDateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private IdempotencyKeyRepository idempotencyKeyRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private TransferTokenRepository transferTokenRepository;

    @Autowired
    private AccountGuard accountGuard;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Value("${jjb.external-transfer.expiry-days}")
    private double externalTransferExpiryDays;

    @Value("${jjb.frontend.base.url}")
    private String frontEndBaseUrl;

    @Transactional
    public AccountView deposit(UUID accountId, BigDecimal amount, String idemKey) {

        Account account = this.getAccount(accountId);

        UUID ownerId = account.getUser().getId();
        accountGuard.requireOwned(ownerId);

        //idemKey sent from client, if not present, continue as if new operation
        if(idemKey != null && !idemKey.isBlank()) {
            if(idempotencyKeyRepository.existsByOwnerIdAndKeyValue(ownerId, idemKey)) {
                //operation already processed - return a generic (idempotent) result
                return new AccountView(account.getId(), account.getType().toString(), account.getBalance());
            }
        }

        accountGuard.requirePositive(amount);
        account.setBalance(account.getBalance().add(amount));

        //persist transaction
        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setType(Transaction.Type.DEPOSIT);
        tx.setAmount(amount);
        tx.setReference("TX-" + Instant.now().toEpochMilli());
        transactionRepository.save(tx);

        //record idempotency after success - if it was sent by client
        if(idemKey != null && !idemKey.isBlank()) {
            IdempotencyKey key = new IdempotencyKey();
            key.setOwnerId(ownerId);
            key.setKeyValue(idemKey);
            idempotencyKeyRepository.save(key);
        }

        return new AccountView(account.getId(), account.getType().toString(), account.getBalance());
    }

    @Transactional
    public AccountView withdraw(UUID accountId, BigDecimal amount, String idemKey) {

        Account account = this.getAccount(accountId);

        UUID ownerId = account.getUser().getId();
        accountGuard.requireOwned(ownerId);

        if(idemKey != null && !idemKey.isBlank()) {
            if(idempotencyKeyRepository.existsByOwnerIdAndKeyValue(ownerId, idemKey)) {
                //operation already processed - return generic (idempotent) result
                return new AccountView(account.getId(), account.getType().toString(), account.getBalance());
            }
        }

        accountGuard.requirePositive(amount);
        accountGuard.requireSufficientFunds(account.getBalance(), amount);
        account.setBalance(account.getBalance().subtract(amount));

        //persist transaction
        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setType(Transaction.Type.WITHDRAW);
        tx.setAmount(amount);
        tx.setReference("TX-" + Instant.now().toEpochMilli());
        transactionRepository.save(tx);

        //record idempotence after success - if sent by client
        if(idemKey != null && !idemKey.isBlank()) {
            IdempotencyKey key = new IdempotencyKey();
            key.setOwnerId(ownerId);
            key.setKeyValue(idemKey);
            idempotencyKeyRepository.save(key);
        }

        return new AccountView(account.getId(), account.getType().toString(), account.getBalance());
    }

    @Transactional
    public InternalTransferResponse internalTransfer(InternalTransferRequest request, String idemKey) {

        //load source account
        Account from = this.getAccount(request.fromAccountId());
        UUID fromUserId = from.getUser().getId();

        if(idemKey != null && !idemKey.isBlank()) {
            if(idempotencyKeyRepository.existsByOwnerIdAndKeyValue(fromUserId, idemKey)) {
                return new InternalTransferResponse(
                        request.fromAccountId(), request.toAccountId(), request.amount(), "Duplicate Request"
                );
            }
        }

        //load destination account
        Account to = this.getAccount(request.toAccountId());
        UUID toUserId = to.getUser().getId();

        //ownership check: can only send from user's own account
        accountGuard.requireOwned(fromUserId);
        //make sure destination account also belong to user
        accountGuard.requireOwned(toUserId);

        //can't transfer to same account
        accountGuard.requireNotSame(from.getId(), to.getId());

        BigDecimal amount = request.amount();
        accountGuard.requirePositive(amount);
        accountGuard.requireSufficientFunds(from.getBalance(), amount);

        //compute shared reference
        String sharedRef = "TX-" + Instant.now().toEpochMilli();

        //execute operations
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        //persist transactions
        Transaction out = new Transaction();
        out.setAccount(from);
        out.setType(Transaction.Type.TRANSFER_OUT);
        out.setAmount(amount);
        out.setReference(sharedRef);
        transactionRepository.save(out);

        Transaction in = new Transaction();
        in.setAccount(to);
        in.setType(Transaction.Type.TRANSFER_IN);
        in.setAmount(amount);
        in.setReference(sharedRef);
        transactionRepository.save(in);

        //record idempotency after success
        if(idemKey != null && !idemKey.isBlank()) {
            IdempotencyKey key = new IdempotencyKey();
            key.setOwnerId(fromUserId);
            key.setKeyValue(idemKey);
            idempotencyKeyRepository.save(key);
        }
        return new InternalTransferResponse(from.getId(), to.getId(), amount, sharedRef);
    }

    @Transactional
    public void initiateExternalTransfer(OutgoingExternalTransferRequest request, String idemKey) {

        //load sender's account
        Account senderAccount = this.getAccount(request.senderAccountId());
        User sender = senderAccount.getUser();

        if(idemKey != null && !idemKey.isBlank()) {
            if(idempotencyKeyRepository.existsByOwnerIdAndKeyValue(sender.getId(), idemKey)) {
                throw new DuplicateTransactionException("This transfer has already been initiated. This is a duplicate transaction.");
            }
        }

        //ensure sender matches current logged in user
        accountGuard.requireOwned(sender.getId());

        //ensure amount is correct
        BigDecimal amount = request.amount();
        accountGuard.requirePositive(amount);
        accountGuard.requireSufficientFunds(senderAccount.getBalance(), amount);

        //compute shared reference
        String sharedRef = "EXT-TX-" + Instant.now().toEpochMilli();

        //take funds from senderAccount
        senderAccount.setBalance(senderAccount.getBalance().subtract(amount));

        //retrieve contact information of the recipient
        Contact recipient = contactRepository.findById(request.contactId())
                .orElseThrow(() -> new NoSuchElementException("No contact was found with the following contact id: " + request.contactId()));

        //persist transactions
        ExternalTransfer out = new ExternalTransfer();
        out.setAccount(senderAccount);
        out.setType(Transaction.Type.TRANSFER_OUT);
        out.setAmount(amount);
        out.setReference(sharedRef);
        out.setStatus(ExternalTransfer.Status.PENDING);
        out.setMessage(request.message());
        Instant expiresAt = out.getCreatedAt().plus(10, ChronoUnit.MINUTES); //change value, this is for testing
        Instant reminderAt = out.getCreatedAt().plus(5, ChronoUnit.MINUTES); //change value, this is for testing
        out.setExpiresAt(expiresAt); //change this is for testing
        out.setReminderAt(reminderAt);
        out.setSecurityQuestion(recipient.getSecurityQuestion());
        out.setSecurityAnswerHash(recipient.getSecurityAnswerHash());
        transactionRepository.save(out);


        //variable to hold recipient name, will change depending on if they are a User of JJBank or not
        String recipientName;

        ExternalTransfer in = new ExternalTransfer();
        //verify if contact is a JJBank user
        Optional<Account> recipientAccount = accountRepository.findFirstByUserEmailOrderByCreatedAtAsc(recipient.getRecipientEmail());
        if(recipientAccount.isPresent()) {
            in.setAccount(recipientAccount.get());
            recipientName = recipientAccount.get().getUser().getFullName();
        } else {
            //the recipient doesn't yet have an account, set to senderAccount for now,
            //will have to delete the record for this ExternalTransfer if recipient never creates account
            //must change it when user accepts the transfer with their new account
            in.setAccount(senderAccount);
            recipientName = recipient.getDisplayName();
        }
        in.setType(Transaction.Type.TRANSFER_IN);
        in.setAmount(amount);
        in.setReference(sharedRef);
        in.setStatus(ExternalTransfer.Status.PENDING);
        in.setMessage(request.message());
        in.setExpiresAt(expiresAt);
        in.setReminderAt(reminderAt);
        in.setSecurityQuestion(recipient.getSecurityQuestion());
        in.setSecurityAnswerHash(recipient.getSecurityAnswerHash());
        transactionRepository.save(in);

        //create and persist transfer token
        TransferToken transferToken = new TransferToken();
        transferToken.setIncomingTransfer(in);
        transferToken.setRecipient(recipient);
        PasswordEncoder encoder = new BCryptPasswordEncoder(12);
        String transferTokenString = UUID.randomUUID().toString();
        transferToken.setTokenHash(encoder.encode(transferTokenString));
        transferTokenRepository.save(transferToken);

        //record idempotency after success
        IdempotencyKey key = new IdempotencyKey();
        key.setOwnerId(sender.getId());
        key.setKeyValue(idemKey);
        idempotencyKeyRepository.save(key);

        //create transfer link with transfer token
        String transferLinkPath = "/transfer/accept/" + transferTokenString;
        String transferLink = frontEndBaseUrl + transferLinkPath;

        //notify recipient
        InstantToDateConverter dateConverter = new InstantToDateConverter(); //make it a member variable when you change for @Autowired constructor injection
        eventPublisher.publishEvent(
                new TransferInitiatedEvent(
                        recipient.getRecipientEmail(),
                        recipientName,
                        dateConverter.toAbbreviatedFullDate(Instant.now()),
                        amount.toPlainString(),
                        sender.getFullName(),
                        sharedRef,
                        transferLink
                )
        );
    }

    private Account getAccount(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalStateException("Account not found"));
    }


}
