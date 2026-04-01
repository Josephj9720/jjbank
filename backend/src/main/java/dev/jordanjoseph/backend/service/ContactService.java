package dev.jordanjoseph.backend.service;

import dev.jordanjoseph.backend.dto.contact.ContactView;
import dev.jordanjoseph.backend.dto.contact.UpdateContactRequest;
import dev.jordanjoseph.backend.exception.ResourceAlreadyExistsException;
import dev.jordanjoseph.backend.model.Contact;
import dev.jordanjoseph.backend.model.User;
import dev.jordanjoseph.backend.repository.ContactRepository;
import dev.jordanjoseph.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ContactService {

    private final ContactRepository contactRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ContactService(ContactRepository contactRepository, UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    @Transactional
    public void addContact(UUID ownerId, String email, String displayName, String securityQuestion, String securityAnswer) {

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NoSuchElementException("The user for which the contact would be created doesn't exist."));


        if(owner.getEmail().equals(email)) {
            throw new IllegalArgumentException("You cannot add yourself as a contact.");
        }

        if(contactRepository.existsByOwnerIdAndEmail(ownerId, email)) {
            throw new ResourceAlreadyExistsException("Contact", "email", email);
        }

        if(displayName.isBlank()) {
            displayName = email;
        }

        Contact contact = new Contact();
        contact.setOwner(owner);
        contact.setEmail(email);
        contact.setDisplayName(displayName);
        contact.setSecurityQuestion(securityQuestion);
        contact.setSecurityAnswerHash(passwordEncoder.encode(securityAnswer));
        contactRepository.save(contact);
    }

    @Transactional
    public void deleteContact(UUID contactId) {
        contactRepository.deleteById(contactId);
    }

    public void updateContact(UpdateContactRequest request) {
        if(!request.displayName().isBlank() && !request.securityQuestion().isBlank() && !request.securityAnswer().isBlank()) {
            updateContact(request.contactId(), request.displayName(), request.securityQuestion(), request.securityAnswer());

        } else if(request.displayName().isBlank() && !request.securityQuestion().isBlank() && !request.securityQuestion().isBlank()) {
            updateContact(request.contactId(), request.securityQuestion(), request.securityAnswer());

        } else if(!request.displayName().isBlank() && (request.securityQuestion().isBlank() || request.securityAnswer().isBlank())) {
            updateContact(request.contactId(), request.displayName());

        }
    }

    @Transactional
    private void updateContact(UUID contactId, String newDisplayName) {
        Contact contactToUpdate = contactRepository.getReferenceById(contactId);
        contactToUpdate.setDisplayName(newDisplayName);
        contactRepository.save(contactToUpdate);
    }

    @Transactional
    private void updateContact(UUID contactId, String newSecurityQuestion, String newSecurityAnswer) {
        Contact contactToUpdate = contactRepository.getReferenceById(contactId);
        contactToUpdate.setSecurityQuestion(newSecurityQuestion);
        contactToUpdate.setSecurityAnswerHash(passwordEncoder.encode(newSecurityAnswer));
        contactRepository.save(contactToUpdate);
    }

    @Transactional
    private void updateContact(UUID contactId, String newDisplayName, String newSecurityQuestion, String newSecurityAnswer) {
        Contact contactToUpdate = contactRepository.getReferenceById(contactId);
        contactToUpdate.setDisplayName(newDisplayName);
        contactToUpdate.setSecurityQuestion(newSecurityQuestion);
        contactToUpdate.setSecurityAnswerHash(passwordEncoder.encode(newSecurityAnswer));
        contactRepository.save(contactToUpdate);
    }



    public Page<ContactView> listContacts(UUID ownerId, Pageable pageable) {
        Page<Contact> page = contactRepository.findByOwnerId(ownerId, pageable);
        return page.map(this::toView);
    }

    private ContactView toView(Contact contact) {
        return new ContactView(contact.getId(), contact.getDisplayName(), contact.getEmail());
    }

}
