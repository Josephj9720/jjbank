package dev.jordanjoseph.backend.repository;

import dev.jordanjoseph.backend.model.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.UUID;

public interface ContactRepository extends JpaRepository<Contact, UUID> {
    Page<Contact> findByOwnerId(UUID ownerId, Pageable pageable);
    boolean existsByOwnerIdAndRecipientId(UUID ownerId, UUID recipientId);
}
