package dev.jordanjoseph.backend.dto.transactionhistory;

import dev.jordanjoseph.backend.model.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** sealed interface: allows to only permit the specified classes to implement the interface
 *  this way they will all be treated similarly through the interface TransactionView
 *  they can all be sent together as TransactionView DTOs
 * */
public sealed interface TransactionView permits BasicTransactionView, TransferInTransactionView, TransferOutTransactionView {
    UUID id();
    Transaction.Type type();
    BigDecimal amount();
    String reference();
    Instant createdAt();
}
