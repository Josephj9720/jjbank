package dev.jordanjoseph.backend.dto.transfer;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferResponse(
        UUID fromAccountId,
        UUID toAccountId,
        BigDecimal amount,
        String reference
) {}
