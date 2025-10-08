package dev.jordanjoseph.backend.util;

import dev.jordanjoseph.backend.config.AuthenticationFacade;
import dev.jordanjoseph.backend.model.Account;

import dev.jordanjoseph.backend.model.UserPrincipal;
import dev.jordanjoseph.backend.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;

import java.util.List;
import java.util.UUID;

@Component
public class AccountValidator {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AuthenticationFacade authenticationFacade;

    private UUID currentUserId() {
        UserPrincipal userPrincipal = (UserPrincipal) authenticationFacade.getAuthentication().getPrincipal();
        return userPrincipal.getId();
    }

    private Collection<? extends GrantedAuthority> currentUserAuthorities() {
        UserPrincipal userPrincipal = (UserPrincipal) authenticationFacade.getAuthentication().getPrincipal();
        return userPrincipal.getAuthorities();
    }

    public List<Account> getCurrentUserAccounts() {
        return accountRepository.findByUserId(currentUserId());
    }

    public Account exist(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalStateException("Account not found"));
    }

    public void requireOwned(UUID userId) {

        if(!userId.equals(currentUserId()) || isAdmin()) {
            System.out.println("here!");
            throw new AccessDeniedException("Not your account");
        }
    }

    public void requireNotSame(UUID from, UUID to) {
        if(from.equals(to)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
    }

    public void requireSufficientFunds(BigDecimal balance, BigDecimal amount) {
        if(balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient Funds");
        }
    }

    public void requirePositive(BigDecimal amount) {
        if(amount.compareTo(new BigDecimal("0.01")) < 0) {
            throw new IllegalArgumentException("Amount must be >= 0.01");
        }
    }

    private boolean isAdmin() {
        return currentUserAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));
    }

    public void requireAdmin() {
        if(!isAdmin()) {
            throw new AccessDeniedException("Admin Access Only");
        }
    }

    public void requireSuperAdmin() {
        if(currentUserAuthorities().stream().noneMatch(
                a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"))) {
            throw new AccessDeniedException("Super Admin Access Only");
        }
    }

}
