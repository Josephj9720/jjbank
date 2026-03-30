package dev.jordanjoseph.backend.dto.transfer.event;

public record TransferRequestedEvent(
        @jakarta.validation.constraints.Email String senderEmail,
        String senderFullName,
        String date,
        String amount,
        String recipientFullName,
        String reference,
        String message,
        String transferLink
) {}
