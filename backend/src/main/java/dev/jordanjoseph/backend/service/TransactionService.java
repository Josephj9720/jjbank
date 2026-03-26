package dev.jordanjoseph.backend.service;

import dev.jordanjoseph.backend.dto.account.AccountView;
import dev.jordanjoseph.backend.dto.common.ApiResult;
import dev.jordanjoseph.backend.dto.transfer.*;

import dev.jordanjoseph.backend.dto.transfer.event.TransferCancelledEvent;
import dev.jordanjoseph.backend.dto.transfer.event.TransferCompletedEvent;
import dev.jordanjoseph.backend.dto.transfer.event.TransferDeclinedEvent;
import dev.jordanjoseph.backend.dto.transfer.event.TransferInitiatedEvent;
import dev.jordanjoseph.backend.exception.DuplicateTransactionException;
import dev.jordanjoseph.backend.model.*;
import dev.jordanjoseph.backend.repository.*;
import dev.jordanjoseph.backend.util.AccountGuard;
import dev.jordanjoseph.backend.util.HashUtil;
import dev.jordanjoseph.backend.util.InstantToDateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
        HashUtil hashUtil = new HashUtil();
        String transferTokenString = UUID.randomUUID().toString();
        transferToken.setTokenHash(hashUtil.sha256(transferTokenString));
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

    @Transactional
    public ApiResult claimOrDeclineExternalTransfer(
            String userEmail,
            ClaimOrDeclineExternalTransferRequest request,
            ClaimOrDeclineExternalTransferRequest.Decision decision,
            String idemKey) {

        //fetch transfer token from database
        HashUtil hashUtil = new HashUtil();
        TransferToken token = transferTokenRepository.findByTokenHash(hashUtil.sha256(request.transferToken()))
                .orElseThrow(() -> new NoSuchElementException("The provided transfer token could not be found."));

        //load recipient's account and user
        ExternalTransfer incomingTransfer = token.getIncomingTransfer();
        Account recipientAccount = this.getAccount(request.recipientAccountId());
        User recipient = recipientAccount.getUser();

        if(idemKey != null && !idemKey.isBlank()) {
            if(idempotencyKeyRepository.existsByOwnerIdAndKeyValue(recipient.getId(), idemKey)) {
                throw new DuplicateTransactionException("This transfer has already been processed. This is a duplicate transaction.");
            }
        }

        //ensure that the User (recipient) owns the account in which they want to deposit
        accountGuard.requireOwned(recipient.getId());

        //verify token validity
        if(token.isInvalid()) {
            throw new AccessDeniedException("The transfer token is invalid.");
        }

        //verify that currently logged in userEmail matches recipient email
        Contact recipientContact = token.getRecipient();
        if(!userEmail.equals(recipientContact.getRecipientEmail())) {
            throw new AccessDeniedException("You are not the recipient of the transfer.");
        }

        //get complementary external transfer before changing the account
        ExternalTransfer outgoingTransfer = this.getComplementaryTransfer(
                incomingTransfer.getReference(),
                incomingTransfer.getAccount().getId(),
                incomingTransfer.getId());

        //verify security answer
        if(incomingTransfer.getFailedSecurityAttempts() < 3) {
            String securityAnswerHash = incomingTransfer.getSecurityAnswerHash();
            String securityAnswerAttempt = request.securityAnswer();
            PasswordEncoder encoder = new BCryptPasswordEncoder(12);
            if(!encoder.matches(securityAnswerAttempt, securityAnswerHash)) {
                int updatedFailedSecurityAttempts = incomingTransfer.getFailedSecurityAttempts() + 1;
                incomingTransfer.setFailedSecurityAttempts(updatedFailedSecurityAttempts);
                if(updatedFailedSecurityAttempts == 3) {
                    //invalidate transfer token
                    token.setInvalid(true);

                    //update expiration Instant
                    incomingTransfer.setExpiresAt(Instant.now());
                    outgoingTransfer.setExpiresAt(Instant.now());
                }
                return new ApiResult(ApiResult.Status.FAILURE, 3 - updatedFailedSecurityAttempts + " attempts left");
            }
        } else {
            throw new AccessDeniedException("Maximum failed security attempts reached.");
        }

        switch (decision) {
            case CLAIM -> this.claimExternalTransfer(
                    recipient,
                    recipientAccount,
                    incomingTransfer,
                    outgoingTransfer,
                    token,
                    idemKey);
            case DECLINE -> this.declineExternalTransfer(
                    recipient,
                    recipientAccount,
                    incomingTransfer,
                    outgoingTransfer,
                    token,
                    idemKey);
        }

        return new ApiResult(ApiResult.Status.SUCCESS, "Transfer claimed successfully.");
    }

    private void claimExternalTransfer(
            User recipient,
            Account recipientAccount,
            ExternalTransfer incomingTransfer,
            ExternalTransfer outgoingTransfer,
            TransferToken token,
            String idemKey) {
        //security challenge passed, now claim transfer
        recipientAccount.setBalance(recipientAccount.getBalance().add(incomingTransfer.getAmount()));

        //assign chosen account to incoming Transfer record
        incomingTransfer.setAccount(recipientAccount);

        //set both record as COMPLETED
        Instant now = Instant.now();
        incomingTransfer.setStatus(ExternalTransfer.Status.COMPLETED);
        incomingTransfer.setCompletedAt(now);
        outgoingTransfer.setStatus(ExternalTransfer.Status.COMPLETED);
        outgoingTransfer.setCompletedAt(now);

        //invalidate transfer token
        token.setInvalid(true);

        //record idempotency after success
        IdempotencyKey key = new IdempotencyKey();
        key.setOwnerId(recipient.getId());
        key.setKeyValue(idemKey);
        idempotencyKeyRepository.save(key);

        //load sender's account
        User sender = outgoingTransfer.getAccount().getUser();

        //load other information required for email
        String recipientEmail = recipient.getEmail();
        String recipientFullName = recipient.getFullName();
        InstantToDateConverter dateConverter = new InstantToDateConverter(); //make it a member variable when you change for @Autowired constructor injection
        String date = dateConverter.toAbbreviatedFullDate(now);
        String amount = incomingTransfer.getAmount().toPlainString();
        String senderEmail = sender.getEmail();
        String senderFullName = sender.getFullName();
        String reference = incomingTransfer.getReference();

        //notify recipient
        eventPublisher.publishEvent(new TransferCompletedEvent(
                "recipient",
                recipientEmail,
                recipientFullName,
                date,
                amount,
                senderFullName,
                reference)
        );

        //notify sender
        eventPublisher.publishEvent(new TransferCompletedEvent(
                "sender",
                senderEmail,
                recipientFullName,
                date,
                amount,
                senderFullName,
                reference)
        );
    }

    private void declineExternalTransfer(
            User recipient,
            Account recipientAccount,
            ExternalTransfer incomingTransfer,
            ExternalTransfer outgoingTransfer,
            TransferToken token,
            String idemKey
    ) {
        //security challenge passed, now decline transfer
        //refund sender
        Account senderAccount = outgoingTransfer.getAccount();
        senderAccount.setBalance(senderAccount.getBalance().add(outgoingTransfer.getAmount()));

        //assign chosen account to incoming Transfer record to link it to recipient
        incomingTransfer.setAccount(recipientAccount);

        //set both records as DECLINED
        Instant now = Instant.now();
        incomingTransfer.setStatus(ExternalTransfer.Status.DECLINED);
        outgoingTransfer.setStatus(ExternalTransfer.Status.DECLINED);

        //invalidate transfer token
        token.setInvalid(true);

        //record idempotency after success
        IdempotencyKey key = new IdempotencyKey();
        key.setOwnerId(recipient.getId());
        key.setKeyValue(idemKey);
        idempotencyKeyRepository.save(key);

        //load sender's account
        User sender = outgoingTransfer.getAccount().getUser();

        //load other information required for email
        String recipientFullName = recipient.getFullName();
        InstantToDateConverter dateConverter = new InstantToDateConverter(); //make it a member variable when you change for @Autowired constructor injection
        String date = dateConverter.toAbbreviatedFullDate(now);
        String amount = incomingTransfer.getAmount().toPlainString();
        String senderEmail = sender.getEmail();
        String senderFullName = sender.getFullName();
        String reference = incomingTransfer.getReference();

        //notify sender
        eventPublisher.publishEvent(new TransferDeclinedEvent(
                senderEmail,
                senderFullName,
                date,
                amount,
                recipientFullName,
                reference)
        );
    }

    @Transactional
    public void cancelExternalTransfer(CancelExternalTransferRequest request, String idemKey) {

        //load outgoing ExternalTransfer and sender's account and User
        ExternalTransfer outgoingTransfer = (ExternalTransfer) transactionRepository.findById(request.outgoingTransferId())
                .orElseThrow(() -> new NoSuchElementException(
                        "No external transfer was found with the following id: " + request.outgoingTransferId())
                );
        Account senderAccount = outgoingTransfer.getAccount();
        User sender = senderAccount.getUser();

        if(idemKey != null && !idemKey.isBlank()) {
            if(idempotencyKeyRepository.existsByOwnerIdAndKeyValue(sender.getId(), idemKey)) {
                throw new DuplicateTransactionException("This transfer has already been cancelled. This is a duplicate transaction.");
            }
        }

        //ensure sender matches currently logged in user
        accountGuard.requireOwned(sender.getId());

        //refund sender
        senderAccount.setBalance(senderAccount.getBalance().add(outgoingTransfer.getAmount()));

        //load incoming transfer
        ExternalTransfer incomingTransfer = this.getComplementaryTransfer(
                outgoingTransfer.getReference(),
                outgoingTransfer.getAccount().getId(),
                outgoingTransfer.getId());

        //cancel the external transfers
        outgoingTransfer.setStatus(ExternalTransfer.Status.CANCELLED);
        incomingTransfer.setStatus(ExternalTransfer.Status.CANCELLED);

        //get sender and recipient account IDs
        UUID senderAccountId = senderAccount.getId();
        UUID recipientAccountId = incomingTransfer.getAccount().getId();

        //set up variable to hold recipient name and email
        String recipientName;
        String recipientEmail;

        //get TransferToken
        TransferToken token = transferTokenRepository.findByIncomingTransferId(incomingTransfer.getId())
                .orElseThrow(() ->
                        new NoSuchElementException("Transfer Token could not be found for external transfer id: " + incomingTransfer.getId()));

        if(senderAccountId.equals(recipientAccountId)) {
            //the transfer was sent before the recipient had registered, delete their record of the transaction
            //only need the record for the user who is a JJBank user
            transferTokenRepository.delete(token);
            transactionRepository.delete(incomingTransfer);

            //set recipient name and email
            recipientName = token.getRecipient().getDisplayName();
            recipientEmail = token.getRecipient().getRecipientEmail();

        } else {
            //the recipient is a JJBank User, keep the record, invalidate token
            token.setInvalid(true);

            //get recipient User
            User recipient = incomingTransfer.getAccount().getUser();

            //set recipient name and email
            recipientName = recipient.getFullName();
            recipientEmail = recipient.getEmail();
        }

        //record idempotency after success
        IdempotencyKey key = new IdempotencyKey();
        key.setOwnerId(sender.getId());
        key.setKeyValue(idemKey);
        idempotencyKeyRepository.save(key);

        //retrieve necessary information to email recipient
        InstantToDateConverter dateConverter = new InstantToDateConverter();
        String date = dateConverter.toAbbreviatedFullDate(Instant.now());
        String amount = outgoingTransfer.getAmount().toPlainString();
        String senderFullName = sender.getFullName();
        String reference = outgoingTransfer.getReference();

        //notify recipient
        eventPublisher.publishEvent(new TransferCancelledEvent(
                recipientEmail,
                recipientName,
                date,
                amount,
                senderFullName,
                reference)
        );
    }

    private ExternalTransfer getComplementaryTransfer(String reference, UUID accountId, UUID transactionId) {
        List<Transaction> transactions = transactionRepository
                .findByReferenceAndAccountIdNot(reference, accountId);

        for(Transaction transaction : transactions) {
            System.out.println(transaction.getType());
            System.out.println(transaction.getId());
        }

        if(transactions.isEmpty()) {
            //the transfer was sent before the recipient had registered with JJBank so the accountId is the same as the sender's
            transactions = transactionRepository
                    .findByReferenceAndAccountIdAndIdNot(reference, accountId, transactionId);

            if(transactions.isEmpty()) {
                throw new IllegalStateException("No complementary transfer found");
            }
        }

        if(transactions.size() > 1) {
            throw new IllegalStateException("Expected one complementary transfer but found multiple");
        }

        return (ExternalTransfer) transactions.getFirst();
    }

    private Account getAccount(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalStateException("Account not found"));
    }


}
