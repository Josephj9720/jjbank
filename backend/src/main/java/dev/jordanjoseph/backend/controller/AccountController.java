package dev.jordanjoseph.backend.controller;

import dev.jordanjoseph.backend.dto.AccountView;
import dev.jordanjoseph.backend.dto.MoneyRequest;
import dev.jordanjoseph.backend.service.AccountService;
import dev.jordanjoseph.backend.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    public AccountView deposit(@PathVariable UUID id, @RequestBody @Valid MoneyRequest request) {
        return transactionService.deposit(id, request.amount());
    }

    @PostMapping("/{id}/withdraw")
    public AccountView withdraw(@PathVariable UUID id, @RequestBody @Valid MoneyRequest request) {
        return transactionService.withdraw(id, request.amount());
    }

}
