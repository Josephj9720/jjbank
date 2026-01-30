package dev.jordanjoseph.backend.dto.transactionhistory;

import dev.jordanjoseph.backend.model.Account;
import dev.jordanjoseph.backend.model.ExternalTransfer;
import dev.jordanjoseph.backend.model.Transaction;

import java.math.BigDecimal;
import java.util.UUID;

public record OutgoingExternalTransfer(
        Transaction.Type type,
        BigDecimal amount,
        String reference,
        String date,
        Account.Type accountType,
        UUID fromAccount,
        String senderName,
        String recipientEmail,
        String recipientName,
        ExternalTransfer.Status status
) implements TransactionView {}
