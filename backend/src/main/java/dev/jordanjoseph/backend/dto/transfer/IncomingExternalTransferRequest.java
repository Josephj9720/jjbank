package dev.jordanjoseph.backend.dto.transfer;

import java.math.BigDecimal;
import java.util.UUID;

public record IncomingExternalTransferRequest(
        UUID recipientAccountId,
        UUID contactId,
        @jakarta.validation.constraints.DecimalMin("0.01") BigDecimal amount,
        @jakarta.validation.constraints.Size(max = 1000) String message
) {}
