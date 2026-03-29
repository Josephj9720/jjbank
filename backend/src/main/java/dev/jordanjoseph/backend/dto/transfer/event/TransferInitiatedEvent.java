package dev.jordanjoseph.backend.dto.transfer.event;

public record TransferInitiatedEvent(
        @jakarta.validation.constraints.Email String recipientEmail,
        String recipientDisplayName,
        String date,
        String amount,
        String senderFullName,
        String sharedRef,
        String message,
        String transferLink
) {}
