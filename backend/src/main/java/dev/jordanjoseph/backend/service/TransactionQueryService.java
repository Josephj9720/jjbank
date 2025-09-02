package dev.jordanjoseph.backend.service;

import dev.jordanjoseph.backend.dto.transactionhistory.BasicTransactionView;
import dev.jordanjoseph.backend.dto.transactionhistory.TransactionView;
import dev.jordanjoseph.backend.dto.transactionhistory.TransferInTransactionView;
import dev.jordanjoseph.backend.dto.transactionhistory.TransferOutTransactionView;
import dev.jordanjoseph.backend.model.Account;
import dev.jordanjoseph.backend.model.Transaction;
import dev.jordanjoseph.backend.repository.TransactionRepository;
import dev.jordanjoseph.backend.util.AccountValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionQueryService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountValidator accountValidator;

    public Page<TransactionView> listForAccount(
            UUID accountId,
            Transaction.Type type,
            Instant from,
            Instant to,
            Pageable pageable) {

        //verify ownership
        Account account = accountValidator.exist(accountId);
        accountValidator.requireOwned(account.getUser().getId()); //passes if admin, might want to rename method

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
        return switch (t.getType()) {
            case DEPOSIT, WITHDRAW
                    -> new BasicTransactionView(
                            t.getId(), t.getType(), t.getAmount(), t.getReference(), t.getCreatedAt());

            case TRANSFER_IN -> {
                Transaction sender = getComplementaryTransaction(t.getReference(), t.getId());
                yield new TransferInTransactionView(
                        t.getId(),
                        t.getType(),
                        t.getAmount(),
                        t.getReference(),
                        t.getCreatedAt(),
                        t.getAccount().getId(),
                        t.getAccount().getUser().getFullName(),
                        t.getAccount().getUser().getEmail(),
                        sender.getAccount().getUser().getFullName());
            }

            case TRANSFER_OUT -> {
                Transaction recipient = getComplementaryTransaction(t.getReference(), t.getId());
                yield new TransferOutTransactionView(
                        t.getId(),
                        t.getType(),
                        t.getAmount(),
                        t.getReference(),
                        t.getCreatedAt(),
                        t.getAccount().getId(),
                        t.getAccount().getUser().getFullName(),
                        t.getAccount().getUser().getEmail(),
                        recipient.getAccount().getUser().getFullName());
            }
        };
    }

    private Transaction getComplementaryTransaction(String reference, UUID transactionId) {
        List<Transaction> transactions = transactionRepository
                .findByReferenceAndAccountIdNot(reference, transactionId);

        if(transactions.isEmpty()) {
            throw new IllegalStateException("No complementary transaction found");
        }

        if(transactions.size() > 1) {
            throw new IllegalStateException("Expected one complimentary transaction but found multiple");
        }

        return transactions.getFirst();
    }

}
