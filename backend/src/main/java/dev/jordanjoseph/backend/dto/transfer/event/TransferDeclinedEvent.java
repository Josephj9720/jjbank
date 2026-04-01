package dev.jordanjoseph.backend.dto.transfer.event;

public record TransferDeclinedEvent(
        @jakarta.validation.constraints.Email String senderEmail,
        String senderFullName,
        String date,
        String amount,
        String recipientFullName,
        String reference
) {}
