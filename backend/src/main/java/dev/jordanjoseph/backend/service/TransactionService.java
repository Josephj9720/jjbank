package dev.jordanjoseph.backend.service;

import dev.jordanjoseph.backend.dto.account.AccountView;
import dev.jordanjoseph.backend.dto.transfer.InternalTransferRequest;
import dev.jordanjoseph.backend.dto.transfer.InternalTransferResponse;

import dev.jordanjoseph.backend.model.Account;
import dev.jordanjoseph.backend.model.IdempotencyKey;
import dev.jordanjoseph.backend.model.Transaction;
import dev.jordanjoseph.backend.repository.AccountRepository;
import dev.jordanjoseph.backend.repository.IdempotencyKeyRepository;
import dev.jordanjoseph.backend.repository.TransactionRepository;
import dev.jordanjoseph.backend.util.AccountGuard;
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
    private AccountGuard accountGuard;

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

    private Account getAccount(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalStateException("Account not found"));
    }


}
