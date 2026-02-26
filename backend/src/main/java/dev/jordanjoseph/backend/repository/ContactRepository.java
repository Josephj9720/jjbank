package dev.jordanjoseph.backend.repository;

import dev.jordanjoseph.backend.model.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {
    Page<Contact> findByOwnerId(UUID ownerId, Pageable pageable);
    boolean existsByOwnerIdAndRecipientId(UUID ownerId, UUID recipientId);
}
