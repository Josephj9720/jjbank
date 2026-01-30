package dev.jordanjoseph.backend.controller;

import dev.jordanjoseph.backend.dto.transfer.InternalTransferRequest;
import dev.jordanjoseph.backend.dto.transfer.InternalTransferResponse;

import dev.jordanjoseph.backend.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/internal/transfer")
    public ResponseEntity<InternalTransferResponse> internalTransfer(
            @RequestBody @Valid InternalTransferRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idemKey) {
        InternalTransferResponse response = transactionService.internalTransfer(request, idemKey);
        return ResponseEntity.ok(response);
    }
}
