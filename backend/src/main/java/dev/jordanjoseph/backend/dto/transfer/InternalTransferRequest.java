package dev.jordanjoseph.backend.dto.transfer;

import java.math.BigDecimal;
import java.util.UUID;

public record InternalTransferRequest(
        UUID fromAccountId,
        UUID toAccountId,
        @jakarta.validation.constraints.DecimalMin("0.01")BigDecimal amount,
        String reference
) {}
