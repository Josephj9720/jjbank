package dev.jordanjoseph.backend.repository;

import dev.jordanjoseph.backend.model.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, UUID> {
    boolean existsByOwnerIdAndKeyValue(UUID ownerId, String keyValue);
}
