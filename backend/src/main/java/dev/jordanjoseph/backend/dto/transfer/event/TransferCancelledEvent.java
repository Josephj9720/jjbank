package dev.jordanjoseph.backend.dto.transfer.event;

public record TransferCancelledEvent(
        @jakarta.validation.constraints.Email String recipientEmail,
        String recipientDisplayName,
        String date,
        String amount,
        String senderFullName,
        String reference
) {}
