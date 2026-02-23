package dev.jordanjoseph.backend.service;

import dev.jordanjoseph.backend.dto.contact.ContactView;
import dev.jordanjoseph.backend.exception.ResourceAlreadyExistsException;
import dev.jordanjoseph.backend.model.Contact;
import dev.jordanjoseph.backend.model.User;
import dev.jordanjoseph.backend.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ContactService {

    private final ContactRepository contactRepository;

    @Autowired
    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Transactional
    public void addContact(User owner, User recipient, String displayName) {

        UUID ownerId = owner.getId();
        UUID recipientId = recipient.getId();

        if(ownerId.equals(recipientId)) {
            throw new IllegalArgumentException("You cannot add yourself as a contact.");
        }

        if(contactRepository.existsByOwnerIdAndRecipientId(ownerId, recipientId)) {
            throw new ResourceAlreadyExistsException("Contact", "recipientId", recipient.getId().toString());
        }

        Contact contact = new Contact();
        contact.setOwner(owner);
        contact.setRecipient(recipient);
        contact.setDisplayName(displayName);
        contactRepository.save(contact);
    }

    @Transactional
    public void addContact(User owner, User recipient) {
        addContact(owner, recipient, recipient.getFullName());
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
