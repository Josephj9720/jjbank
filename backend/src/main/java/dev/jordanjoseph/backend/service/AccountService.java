package dev.jordanjoseph.backend.service;

import dev.jordanjoseph.backend.config.AuthenticationFacade;
import dev.jordanjoseph.backend.dto.AccountView;
import dev.jordanjoseph.backend.model.Account;
import dev.jordanjoseph.backend.model.User;
import dev.jordanjoseph.backend.model.UserPrincipal;
import dev.jordanjoseph.backend.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    private UUID currentUserId() {
        // Spring Security principal can only be retrieved as Object. Must cast to correct UserPrincipal() instance.
        UserPrincipal userPrincipal = (UserPrincipal) authenticationFacade.getAuthentication().getPrincipal();
        return userPrincipal.getId();
    }

    @Transactional
    public AccountView myAccount() {
        Account account = accountRepository.findByUserId(currentUserId())
                .orElseThrow(() -> new IllegalStateException("Account not found"));
        return new AccountView(account.getId(), account.getBalance());
    }

    @Transactional
    public AccountView deposit(UUID accountId, BigDecimal amount) {
        requirePositive(amount);
        Account account = requireOwned(accountId);
        account.setBalance(account.getBalance().add(amount));
        return new AccountView(account.getId(), account.getBalance());
    }

    @Transactional
    public AccountView withdraw(UUID accountId, BigDecimal amount) {
        requirePositive(amount);
        Account account = requireOwned(accountId);
        if(account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        account.setBalance(account.getBalance().subtract(amount));
        return new AccountView(account.getId(), account.getBalance());
    }

    @Transactional
    public void createForUser(User user) {
        //call right after registration (MVP: one account per user)
        if(accountRepository.findByUserId(user.getId()).isEmpty()) {
            Account account = new Account();
            account.setUser(user);
            accountRepository.save(account);
        }
    }

    private Account requireOwned(UUID accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow();
        if(!account.getUser().getId().equals(currentUserId())) {
            throw new AccessDeniedException("Not your account");
        }
        return account;
    }

    private void requirePositive(BigDecimal amount) {
        if(amount == null || amount.compareTo(new BigDecimal("0.01")) < 0) {
            throw new IllegalArgumentException("Amount must be >= 0.01");
        }
    }

}
