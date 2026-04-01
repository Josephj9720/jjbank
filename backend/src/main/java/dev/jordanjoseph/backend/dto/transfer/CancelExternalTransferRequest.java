package dev.jordanjoseph.backend.dto.transfer;

import java.util.UUID;

public record CancelExternalTransferRequest(
   UUID outgoingTransferId
) {}
