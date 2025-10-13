package dev.jordanjoseph.backend.controller;

import dev.jordanjoseph.backend.dto.account.AccountView;
import dev.jordanjoseph.backend.dto.account.CreateRequest;
import dev.jordanjoseph.backend.dto.account.MoneyRequest;
import dev.jordanjoseph.backend.model.UserPrincipal;
import dev.jordanjoseph.backend.service.AccountService;
import dev.jordanjoseph.backend.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/me")
    public List<AccountView> me() {
        return accountService.myAccounts();
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<AccountView> deposit(
            @PathVariable UUID id,
            @RequestBody @Valid MoneyRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idemKey) {
        AccountView response = transactionService.deposit(id, request.amount(), idemKey);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<AccountView> withdraw(
            @PathVariable UUID id,
            @RequestBody @Valid MoneyRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idemKey) {
        AccountView response = transactionService.withdraw(id, request.amount(), idemKey);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<Void> create(
            Authentication authentication,
            @RequestBody CreateRequest request) {

        if(request.userId() == null) {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            accountService.createForUser(principal.getId(), request.accountType());
        } else {
            accountService.createForUser(request.userId(), request.accountType());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
