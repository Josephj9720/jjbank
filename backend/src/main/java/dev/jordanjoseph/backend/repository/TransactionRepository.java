package dev.jordanjoseph.backend.repository;

import dev.jordanjoseph.backend.model.Transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByReferenceAndAccountIdNot(String reference, UUID accountId);

    Page<Transaction> findByAccountId(UUID accountId, Pageable pageable);

    Page<Transaction> findByAccountIdAndType(UUID accountId, Transaction.Type type, Pageable pageable);

    Page<Transaction> findByAccountIdAndCreatedAtBetween(UUID accountId, Instant from, Instant to, Pageable pageable);

    Page<Transaction> findByAccountIdAndTypeAndCreatedAtBetween(UUID accountId, Transaction.Type type, Instant from, Instant to, Pageable pageable);

}
