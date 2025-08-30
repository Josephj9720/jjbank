package dev.jordanjoseph.backend.service;

import dev.jordanjoseph.backend.dto.AccountView;
import dev.jordanjoseph.backend.model.Account;
import dev.jordanjoseph.backend.model.IdempotencyKey;
import dev.jordanjoseph.backend.repository.AccountRepository;
import dev.jordanjoseph.backend.repository.IdempotencyKeyRepository;
import dev.jordanjoseph.backend.repository.TransactionRepository;
import dev.jordanjoseph.backend.util.AccountValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

        //record idempotence after success - if sent by client
        if(idemKey != null && !idemKey.isBlank()) {
            IdempotencyKey key = new IdempotencyKey();
            key.setOwnerId(ownerId);
            key.setKeyValue(idemKey);
            idempotencyKeyRepository.save(key);
        }

        return new AccountView(account.getId(), account.getBalance());
    }


}
