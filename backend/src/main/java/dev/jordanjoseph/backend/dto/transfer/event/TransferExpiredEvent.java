package dev.jordanjoseph.backend.dto.transfer.event;

public record TransferExpiredEvent(
        String emailTo,
        @jakarta.validation.constraints.Email String email,
        String recipientDisplayName,
        String date,
        String amount,
        String senderFullName,
        String reference
) {}
