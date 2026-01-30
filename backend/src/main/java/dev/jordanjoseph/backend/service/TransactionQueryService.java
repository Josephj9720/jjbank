package dev.jordanjoseph.backend.service;

import dev.jordanjoseph.backend.dto.transactionhistory.*;
import dev.jordanjoseph.backend.model.ExternalTransfer;
import dev.jordanjoseph.backend.model.Transaction;
import dev.jordanjoseph.backend.repository.AccountRepository;
import dev.jordanjoseph.backend.repository.TransactionRepository;
import dev.jordanjoseph.backend.util.AccountGuard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

    public Page<TransactionView> listForAccount(
            UUID accountId,
            Transaction.Type type,
            Instant from,
            Instant to,
            Pageable pageable) {

        //verify ownership
        accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalStateException("Account not found"));
        accountGuard.requireOwned(accountId); //passes if admin, might want to rename method

        //normalize time filters/params
        if(from == null) from = Instant.EPOCH; //1, Jan, 1970
        if(to == null) to = Instant.now();

        Page<Transaction> page = null;
        if(type == null) {
            page = transactionRepository
                    .findByAccountIdAndCreatedAtBetween(accountId, from, to, pageable);

        } else {
            page = transactionRepository
                    .findByAccountIdAndTypeAndCreatedAtBetween(accountId, type, from, to ,pageable);
        }

        return page.map(this::toView); //method reference, returns Page<TransactionView>
    }

    private TransactionView toView(Transaction t) {
        return switch (t) {
            case ExternalTransfer external -> externalTransferToView(external);
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

    private TransactionView externalTransferToView(ExternalTransfer t) {
        return switch (t.getType()) {
            case TRANSFER_IN -> {
                Transaction sender = getComplementaryTransaction(t.getReference(), t.getAccount().getId());
                yield new IncomingExternalTransferView(
                        t.getType(),
                        t.getAmount(),
                        t.getReference(),
                        getDateFromInstant(t.getCreatedAt()),
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
                        getDateFromInstant(t.getCreatedAt()),
                        t.getAccount().getType(),
                        t.getAccount().getId(),
                        t.getAccount().getUser().getFullName(),
                        recipient.getAccount().getUser().getEmail(),
                        recipient.getAccount().getUser().getFullName(),
                        t.getStatus());
            }
            default -> throw new IllegalStateException("Unexpected value: " + t.getType());
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
            throw new IllegalStateException("Expected one complimentary transaction but found multiple");
        }

        return transactions.getFirst();
    }

    private String getDateFromInstant(Instant instant) {
        LocalDate localDate = LocalDate.ofInstant(instant, ZoneId.systemDefault());

        //format date (EE, MMM dd, yyyy)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EE, MMM dd, yyyy");
        localDate.format(formatter);

        return localDate.toString();
    }

}
