package dev.jordanjoseph.backend.repository;

import dev.jordanjoseph.backend.model.ExternalTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExternalTransferRepository extends JpaRepository<ExternalTransfer, UUID> {
    List<ExternalTransfer> findByStatusAndExpiresAtBefore(ExternalTransfer.Status status, Instant time);
    List<ExternalTransfer> findByStatusAndReminderAtBeforeAndReminderSentFalse(ExternalTransfer.Status status, Instant time);
}
