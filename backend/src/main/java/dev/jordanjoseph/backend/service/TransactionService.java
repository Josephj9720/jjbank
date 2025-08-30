package dev.jordanjoseph.backend.service;

import dev.jordanjoseph.backend.dto.AccountView;
import dev.jordanjoseph.backend.model.Account;
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
    public AccountView deposit(UUID accountId, BigDecimal amount) {
        Account account = accountValidator.exist(accountId);
        accountValidator.requireOwned(account.getUser().getId());
        accountValidator.requirePositive(amount);
        account.setBalance(account.getBalance().add(amount));
        return new AccountView(account.getId(), account.getBalance());
    }

    @Transactional
    public AccountView withdraw(UUID accountId, BigDecimal amount) {
        Account account = accountValidator.exist(accountId);
        accountValidator.requireOwned(account.getUser().getId());
        accountValidator.requirePositive(amount);
        accountValidator.requireSufficientFunds(account.getBalance(), amount);
        account.setBalance(account.getBalance().subtract(amount));
        return new AccountView(account.getId(), account.getBalance());
    }


}
