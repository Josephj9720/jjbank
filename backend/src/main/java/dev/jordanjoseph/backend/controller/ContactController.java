package dev.jordanjoseph.backend.controller;

import dev.jordanjoseph.backend.dto.contact.AddContactRequest;
import dev.jordanjoseph.backend.dto.contact.ContactView;
import dev.jordanjoseph.backend.dto.contact.DeleteContactRequest;
import dev.jordanjoseph.backend.dto.contact.UpdateContactRequest;
import dev.jordanjoseph.backend.model.UserPrincipal;
import dev.jordanjoseph.backend.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService contactService;

    private final PagedResourcesAssembler<ContactView> assembler;

    @Autowired
    public ContactController(ContactService contactService, PagedResourcesAssembler<ContactView> assembler) {
        this.contactService = contactService;
        this.assembler = assembler;
    }

    @PostMapping
    public ResponseEntity<Void> addContact(
            Authentication authentication,
            @RequestBody AddContactRequest request) {

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        contactService.addContact(principal.getId(), request.recipientEmail(), request.displayName());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<Void> updateContact(@RequestBody UpdateContactRequest request) {
        contactService.updateContact(request.contactId(), request.displayName());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteContact(@RequestBody DeleteContactRequest request) {
        contactService.deleteContact(request.contactId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ContactView>>> getContacts(
            Authentication authentication,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Page<ContactView> contacts = contactService.listContacts(principal.getId(), pageable);
        return ResponseEntity.ok(assembler.toModel(contacts));
    }

}
