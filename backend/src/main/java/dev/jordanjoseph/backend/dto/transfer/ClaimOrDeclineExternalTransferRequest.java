package dev.jordanjoseph.backend.dto.transfer;

import java.util.UUID;

public record ClaimOrDeclineExternalTransferRequest(
    String action,
    String transferToken,
    String securityAnswer,
    UUID recipientAccountId
) {
    public enum Action { CLAIM, DECLINE }
}
