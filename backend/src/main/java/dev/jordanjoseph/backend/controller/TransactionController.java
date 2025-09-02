package dev.jordanjoseph.backend.controller;

import dev.jordanjoseph.backend.dto.transfer.TransferRequest;
import dev.jordanjoseph.backend.dto.transfer.TransferResponse;
import dev.jordanjoseph.backend.model.Transaction;
import dev.jordanjoseph.backend.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    
    //putting it here allows for extension with future support for multiple accounts per user
    @PostMapping("/transactions/transfer")
    public ResponseEntity<TransferResponse> transfer(
            @RequestBody @Valid TransferRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idemKey) {
        TransferResponse response = transactionService.transfer(request, idemKey);
        return ResponseEntity.ok(response);
    }
}
