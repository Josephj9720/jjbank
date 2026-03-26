package dev.jordanjoseph.backend.controller;

import dev.jordanjoseph.backend.dto.common.ApiResult;
import dev.jordanjoseph.backend.dto.transfer.ClaimOrDeclineExternalTransferRequest;
import dev.jordanjoseph.backend.dto.transfer.InternalTransferRequest;
import dev.jordanjoseph.backend.dto.transfer.InternalTransferResponse;

import dev.jordanjoseph.backend.dto.transfer.OutgoingExternalTransferRequest;
import dev.jordanjoseph.backend.model.UserPrincipal;
import dev.jordanjoseph.backend.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @PostMapping("/external/transfer")
    public ResponseEntity<Void> externalTransfer(
            @RequestBody @Valid OutgoingExternalTransferRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idemKey) {
        transactionService.initiateExternalTransfer(request, idemKey);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/external/transfer/claim")
    public ResponseEntity<String> claimExternalTransfer(
            Authentication authentication,
            @RequestBody ClaimOrDeclineExternalTransferRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idemKey) {

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        ApiResult result = transactionService.claimOrDeclineExternalTransfer(
                principal.getUsername(),
                request,
                idemKey);

        if(result.status().equals(ApiResult.Status.SUCCESS)) {
            return ResponseEntity.ok(result.message());
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(result.message());
        }
    }
}
