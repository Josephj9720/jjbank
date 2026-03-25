package dev.jordanjoseph.backend.dto.transfer.event;

public record TransferCompletedEvent(
    String emailTo,
    @jakarta.validation.constraints.Email String email,
    String recipientFullName,
    String date,
    String amount,
    String senderFullName,
    String reference
) {}
