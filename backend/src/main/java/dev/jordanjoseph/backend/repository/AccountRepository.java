package dev.jordanjoseph.backend.repository;

import dev.jordanjoseph.backend.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<Account> findByUserId(UUID userId);
    long countByUserId(UUID userId);
}
