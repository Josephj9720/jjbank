package dev.jordanjoseph.backend.controller;

import dev.jordanjoseph.backend.dto.AccountView;
import dev.jordanjoseph.backend.dto.MoneyRequest;
import dev.jordanjoseph.backend.dto.TransferRequest;
import dev.jordanjoseph.backend.dto.TransferResponse;
import dev.jordanjoseph.backend.service.AccountService;
import dev.jordanjoseph.backend.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/me")
    public AccountView me() {
        return accountService.myAccount();
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

}
