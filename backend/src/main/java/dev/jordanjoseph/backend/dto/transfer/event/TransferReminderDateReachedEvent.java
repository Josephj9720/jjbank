package dev.jordanjoseph.backend.dto.transfer.event;

public record TransferReminderDateReachedEvent(
        @jakarta.validation.constraints.Email String recipientEmail,
        String recipientDisplayName,
        String date,
        String expiry,
        String amount,
        String senderFullName,
        String sharedRef,
        String message,
        String transferLink
) {}
