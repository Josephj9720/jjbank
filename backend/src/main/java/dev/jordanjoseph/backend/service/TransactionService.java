package dev.jordanjoseph.backend.service;

import dev.jordanjoseph.backend.dto.AccountView;
import dev.jordanjoseph.backend.dto.TransferRequest;
import dev.jordanjoseph.backend.dto.TransferResponse;
import dev.jordanjoseph.backend.model.Account;
import dev.jordanjoseph.backend.model.IdempotencyKey;
import dev.jordanjoseph.backend.model.Transaction;
import dev.jordanjoseph.backend.repository.AccountRepository;
import dev.jordanjoseph.backend.repository.IdempotencyKeyRepository;
import dev.jordanjoseph.backend.repository.TransactionRepository;
import dev.jordanjoseph.backend.util.AccountValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
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
    private AccountValidator accountValidator;

    @Transactional
    public AccountView deposit(UUID accountId, BigDecimal amount, String idemKey) {

        Account account = accountValidator.exist(accountId);

        UUID ownerId = account.getUser().getId();
        accountValidator.requireOwned(ownerId);

        //idemKey sent from client, if not present, continue as if new operation
        if(idemKey != null && !idemKey.isBlank()) {
            if(idempotencyKeyRepository.existsByOwnerIdAndKeyValue(ownerId, idemKey)) {
                //operation already processed - return a generic (idempotent) result
                return new AccountView(account.getId(), account.getBalance());
            }
        }

        accountValidator.requirePositive(amount);
        account.setBalance(account.getBalance().add(amount));

        //persist transaction
        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setType(Transaction.Type.DEPOSIT);
        tx.setAmount(amount);
        tx.setReference("TX-" + Instant.now().toEpochMilli());

        //record idempotency after success - if it was sent by client
        if(idemKey != null && !idemKey.isBlank()) {
            IdempotencyKey key = new IdempotencyKey();
            key.setOwnerId(ownerId);
            key.setKeyValue(idemKey);
            idempotencyKeyRepository.save(key);
        }

        return new AccountView(account.getId(), account.getBalance());
    }

    @Transactional
    public AccountView withdraw(UUID accountId, BigDecimal amount, String idemKey) {

        Account account = accountValidator.exist(accountId);

        UUID ownerId = account.getUser().getId();
        accountValidator.requireOwned(ownerId);

        if(idemKey != null && !idemKey.isBlank()) {
            if(idempotencyKeyRepository.existsByOwnerIdAndKeyValue(ownerId, idemKey)) {
                //operation already processed - return generic (idempotent) result
                return new AccountView(account.getId(), account.getBalance());
            }
        }

        accountValidator.requirePositive(amount);
        accountValidator.requireSufficientFunds(account.getBalance(), amount);
        account.setBalance(account.getBalance().subtract(amount));

        //

        //record idempotence after success - if sent by client
        if(idemKey != null && !idemKey.isBlank()) {
            IdempotencyKey key = new IdempotencyKey();
            key.setOwnerId(ownerId);
            key.setKeyValue(idemKey);
            idempotencyKeyRepository.save(key);
        }

        return new AccountView(account.getId(), account.getBalance());
    }

    @Transactional
    public TransferResponse transfer(TransferRequest request, String idemKey) {

        //load sender account
        Account from = accountValidator.exist(request.fromAccountId());
        UUID senderId = from.getUser().getId();

        if(idemKey != null && !idemKey.isBlank()) {
            if(idempotencyKeyRepository.existsByOwnerIdAndKeyValue(senderId, idemKey)) {
                return new TransferResponse(
                        request.fromAccountId(), request.toAccountId(), request.amount(), request.reference()
                );
            }
        }

        //load recipient account
        Account to = accountValidator.exist(request.toAccountId());

        //ownership check: can only send from sender's own account
        accountValidator.requireOwned(senderId);

        //can't transfer to same account
        accountValidator.requireNotSame(from.getId(), to.getId());

        BigDecimal amount = request.amount();
        accountValidator.requirePositive(amount);
        accountValidator.requireSufficientFunds(from.getBalance(), amount);

        //compute shared reference, if not sent by client, create reference
        String sharedRef = request.reference() != null && !request.reference().isBlank()
                ? request.reference()
                : "TX-" + Instant.now().toEpochMilli();

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
            key.setOwnerId(senderId);
            key.setKeyValue(idemKey);
            idempotencyKeyRepository.save(key);
        }
        return new TransferResponse(from.getId(), to.getId(), amount, sharedRef);
    }


}
