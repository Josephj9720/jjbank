package dev.jordanjoseph.backend.dto.transfer;

import java.util.UUID;

public record AcceptExternalTransferRequest(
        UUID outgoingTransferId,
        UUID senderAccountId
) {}
