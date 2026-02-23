package dev.jordanjoseph.backend.service;

import dev.jordanjoseph.backend.dto.contact.ContactView;
import dev.jordanjoseph.backend.exception.BusinessException;
import dev.jordanjoseph.backend.exception.ResourceAlreadyExistsException;
import dev.jordanjoseph.backend.model.Contact;
import dev.jordanjoseph.backend.model.User;
import dev.jordanjoseph.backend.repository.ContactRepository;
import dev.jordanjoseph.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ContactService {

    private final ContactRepository contactRepository;

    private final UserRepository userRepository;

    @Autowired
    public ContactService(ContactRepository contactRepository, UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void addContact(UUID ownerId, String recipientEmail, String displayName) {

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NoSuchElementException("The user for which the contact would be created doesn't exist."));

        User recipient = userRepository.findByEmail(recipientEmail)
                .orElseThrow(() -> new NoSuchElementException("The recipient is not a user of JJBank."));

        UUID recipientId = recipient.getId();

        if(ownerId.equals(recipientId)) {
            throw new IllegalArgumentException("You cannot add yourself as a contact.");
        }

        if(contactRepository.existsByOwnerIdAndRecipientId(ownerId, recipientId)) {
            throw new ResourceAlreadyExistsException("Contact", "recipientId", recipientId.toString());
        }

        if(displayName.isBlank()) {
            displayName = recipient.getFullName();
        }

        Contact contact = new Contact();
        contact.setOwner(owner);
        contact.setRecipient(recipient);
        contact.setDisplayName(displayName);
        contactRepository.save(contact);
    }

    @Transactional
    public void deleteContact(UUID contactId) {
        contactRepository.deleteById(contactId);
    }

    @Transactional
    public void updateContact(UUID contactId, String newDisplayName) {
        Contact contactToUpdate = contactRepository.getReferenceById(contactId);
        contactToUpdate.setDisplayName(newDisplayName);
        contactRepository.save(contactToUpdate);
    }

    public Page<ContactView> listContacts(UUID ownerId, Pageable pageable) {
        Page<Contact> page = contactRepository.findByOwnerId(ownerId, pageable);
        return page.map(this::toView);
    }

    private ContactView toView(Contact contact) {
        return new ContactView(contact.getId(), contact.getDisplayName(), contact.getRecipient().getEmail());
    }

}
