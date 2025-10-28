package dev.jordanjoseph.backend.repository;

import dev.jordanjoseph.backend.model.DebitCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DebitCardRepository extends JpaRepository<DebitCard, UUID> {
    Optional<DebitCard> findByNumber(String number);
}
