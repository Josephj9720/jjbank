package dev.jordanjoseph.backend.controller;

import dev.jordanjoseph.backend.dto.common.ApiResult;
import dev.jordanjoseph.backend.dto.transfer.*;

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
                ClaimOrDeclineExternalTransferRequest.Decision.CLAIM,
                idemKey);

        if(result.status().equals(ApiResult.Status.SUCCESS)) {
            return ResponseEntity.ok(result.message());
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(result.message());
        }
    }

    @PostMapping("/external/transfer/decline")
    public ResponseEntity<String> declineExternalTransfer(
            Authentication authentication,
            @RequestBody ClaimOrDeclineExternalTransferRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idemKey) {

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        ApiResult result = transactionService.claimOrDeclineExternalTransfer(
                principal.getUsername(),
                request,
                ClaimOrDeclineExternalTransferRequest.Decision.DECLINE,
                idemKey);

        if(result.status().equals(ApiResult.Status.SUCCESS)) {
            return ResponseEntity.ok(result.message());
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(result.message());
        }
    }

    @PostMapping("/external/transfer/cancel")
    public ResponseEntity<Void> cancelExternalTransfer(
            @RequestBody CancelExternalTransferRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idemKey) {
        transactionService.cancelExternalTransfer(request, idemKey);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/external/transfer/request")
    public ResponseEntity<Void> requestExternalTransfer(
            @RequestBody IncomingExternalTransferRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idemKey){
        transactionService.requestExternalTransfer(request, idemKey);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
