package dev.jordanjoseph.backend.repository;

import dev.jordanjoseph.backend.model.TransferToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransferTokenRepository extends JpaRepository<TransferToken, UUID> {
    Optional<TransferToken> findByTokenHash(String tokenHash);
    Optional<TransferToken> findByExternalTransferId(UUID externalTransferId);
}
