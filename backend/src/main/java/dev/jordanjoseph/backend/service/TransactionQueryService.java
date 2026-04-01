package dev.jordanjoseph.backend.service;

import dev.jordanjoseph.backend.dto.transactionhistory.*;
import dev.jordanjoseph.backend.model.Account;
import dev.jordanjoseph.backend.model.ExternalTransfer;
import dev.jordanjoseph.backend.model.Transaction;
import dev.jordanjoseph.backend.repository.AccountRepository;
import dev.jordanjoseph.backend.repository.TransactionRepository;
import dev.jordanjoseph.backend.util.AccountGuard;
import dev.jordanjoseph.backend.util.InstantToDateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static java.lang.System.*;

@Service
public class TransactionQueryService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountGuard accountGuard;

    public Page<TransactionView> listCompletedTransactionsForAccount(
            UUID accountId,
            Transaction.Type type,
            Instant from,
            Instant to,
            Pageable pageable) {

        //verify ownership
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalStateException("Account not found"));
        UUID userId = account.getUser().getId();
        accountGuard.requireOwned(userId); //passes if admin, might want to rename method

        //normalize time filters/params
        if(from == null) from = Instant.EPOCH; //1, Jan, 1970
        if(to == null) to = Instant.now();

        Page<Transaction> page = transactionRepository
                .findAndFilterCompletedTransactions(accountId, type, from, to, pageable);

        return page.map(this::toView); //method reference, returns Page<TransactionView>
    }

    private TransactionView toView(Transaction t) {
        return switch (t) {
            case ExternalTransfer external -> completedExternalTransferToView(external);
            case Transaction internal -> internalTransactionToView(internal);
        };
    }

    private TransactionView internalTransactionToView(Transaction t) {
        return switch (t.getType()) {
            case DEPOSIT, WITHDRAW
                    -> new BasicTransactionView(
                        t.getId(),
                        t.getType(),
                        t.getAmount(),
                        t.getReference(),
                        getDateFromInstant(t.getCreatedAt()));

            case TRANSFER_IN -> {
                Transaction sender = getComplementaryTransaction(t.getReference(), t.getAccount().getId());
                yield new IncomingInternalTransferView(
                        t.getType(),
                        t.getAmount(),
                        t.getReference(),
                        getDateFromInstant(t.getCreatedAt()),
                        sender.getAccount().getType(),
                        sender.getAccount().getId());
            }

            case TRANSFER_OUT -> {
                Transaction recipient = getComplementaryTransaction(t.getReference(), t.getAccount().getId());
                yield new OutgoingInternalTransferView(
                        t.getType(),
                        t.getAmount(),
                        t.getReference(),
                        getDateFromInstant(t.getCreatedAt()),
                        recipient.getAccount().getType(),
                        recipient.getAccount().getId());
            }
        };
    }

    private TransactionView completedExternalTransferToView(ExternalTransfer t) {
        if(t.getStatus() != ExternalTransfer.Status.COMPLETED) {
            throw new IllegalStateException("Unexpected 'Status' value for ExternalTransfer: " + t.getStatus());
        }
        return switch (t.getType()) {
            case TRANSFER_IN -> {

                Transaction sender = getComplementaryTransaction(t.getReference(), t.getAccount().getId());
                yield new IncomingExternalTransferView(
                        t.getType(),
                        t.getAmount(),
                        t.getReference(),
                        getDateFromInstant(t.getCompletedAt()),
                        t.getAccount().getType(),
                        t.getAccount().getId(),
                        t.getAccount().getUser().getFullName(),
                        t.getAccount().getUser().getEmail(),
                        sender.getAccount().getUser().getFullName(),
                        t.getStatus());
            }

            case TRANSFER_OUT -> {
                Transaction recipient = getComplementaryTransaction(t.getReference(), t.getAccount().getId());
                yield new OutgoingExternalTransfer(
                        t.getType(),
                        t.getAmount(),
                        t.getReference(),
                        getDateFromInstant(t.getCompletedAt()),
                        t.getAccount().getType(),
                        t.getAccount().getId(),
                        t.getAccount().getUser().getFullName(),
                        recipient.getAccount().getUser().getEmail(),
                        recipient.getAccount().getUser().getFullName(),
                        t.getStatus());
            }
            default -> throw new IllegalStateException("Unexpected 'Type' value for ExternalTransfer: " + t.getType());
        };
    }

    private Transaction getComplementaryTransaction(String reference, UUID accountId) {
        List<Transaction> transactions = transactionRepository
                .findByReferenceAndAccountIdNot(reference, accountId);

        for(Transaction transaction : transactions) {
            out.println(transaction.getType());
            out.println(transaction.getId());
        }

        if(transactions.isEmpty()) {
            throw new IllegalStateException("No complementary transaction found");
        }

        if(transactions.size() > 1) {
            throw new IllegalStateException("Expected one complementary transaction but found multiple");
        }

        return transactions.getFirst();
    }

    private String getDateFromInstant(Instant instant) {
        InstantToDateConverter converter = new InstantToDateConverter();
        return converter.toShortWeekdayLongDate(instant);
    }

}
