package dev.jordanjoseph.backend.dto.contact;

public record AddContactRequest(
        String email,
        String displayName,
        String securityQuestion,
        String securityAnswer
) {}
