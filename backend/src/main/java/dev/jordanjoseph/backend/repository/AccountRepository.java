package dev.jordanjoseph.backend.repository;

import dev.jordanjoseph.backend.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findFirstByUserEmailOrderByCreatedAtAsc(String email);
    List<Account> findByUserId(UUID userId);
    long countByUserId(UUID userId);
}
