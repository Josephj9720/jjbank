package dev.jordanjoseph.backend.dto.transfer;

import java.util.UUID;

public record ClaimExternalTransferRequest(
    String transferToken,
    String securityAnswer,
    UUID recipientAccountId
) {}
