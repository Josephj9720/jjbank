package dev.jordanjoseph.backend.dto.contact;

public record AddContactRequest(
        String recipientEmail,
        String displayName,
        String securityQuestion,
        String securityAnswer
) {}
