package dev.jordanjoseph.backend.dto.transfer;

import java.util.UUID;

public record ClaimOrDeclineExternalTransferRequest(
    String transferToken,
    String securityAnswer,
    UUID recipientAccountId
) {}
