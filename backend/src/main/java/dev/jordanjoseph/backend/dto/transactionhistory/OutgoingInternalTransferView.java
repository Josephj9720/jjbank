package dev.jordanjoseph.backend.dto.transactionhistory;

import dev.jordanjoseph.backend.model.Account;
import dev.jordanjoseph.backend.model.Transaction;

import java.math.BigDecimal;
import java.util.UUID;

/** records can implement interfaces, auto method implementation */
public record OutgoingInternalTransferView(
        Transaction.Type type,
        BigDecimal amount,
        String reference,
        String date,
        Account.Type accountType,
        UUID debitedFromAccount
) implements TransactionView {}
