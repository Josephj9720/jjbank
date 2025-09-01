package dev.jordanjoseph.backend.dto.transactionhistory;

import dev.jordanjoseph.backend.model.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** records can implement interfaces, auto method implementation */
public record TransferInTransactionView(
        UUID id,
        Transaction.Type type,
        BigDecimal amount,
        String reference,
        Instant createdAt,
        UUID recipientId,
        String recipientName,
        String recipientEmail,
        String senderName
) implements TransactionView {}
