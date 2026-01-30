package dev.jordanjoseph.backend.dto.transactionhistory;

import dev.jordanjoseph.backend.model.ExternalTransfer;
import dev.jordanjoseph.backend.model.Transaction;

import java.math.BigDecimal;
import java.util.UUID;

public record IncomingExternalTransferView(
        Transaction.Type type,
        BigDecimal amount,
        String reference,
        String date,
        UUID toAccount,
        String recipientName,
        String recipientEmail,
        String senderName,
        ExternalTransfer.Status status
) implements TransactionView {}
