package dev.jordanjoseph.backend.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(
        UUID fromAccountId,
        UUID toAccountId,
        @jakarta.validation.constraints.DecimalMin("0.01")BigDecimal amount,
        String reference
) {}
