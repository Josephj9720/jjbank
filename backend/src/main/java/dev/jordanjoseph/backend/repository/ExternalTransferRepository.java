package dev.jordanjoseph.backend.repository;

import dev.jordanjoseph.backend.model.ExternalTransfer;
import dev.jordanjoseph.backend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExternalTransferRepository extends JpaRepository<ExternalTransfer, UUID> {
    List<ExternalTransfer> findByReferenceAndAccountIdNot(String reference, UUID accountId);
    List<ExternalTransfer> findByReferenceAndAccountIdAndIdNot(String reference, UUID accountId, UUID id);
    List<ExternalTransfer> findByStatusAndTypeAndExpiresAtBefore(ExternalTransfer.Status status, Transaction.Type type, Instant time);
    List<ExternalTransfer> findByStatusAndTypeAndReminderAtBeforeAndReminderSentFalse(ExternalTransfer.Status status, Transaction.Type type, Instant time);
}
