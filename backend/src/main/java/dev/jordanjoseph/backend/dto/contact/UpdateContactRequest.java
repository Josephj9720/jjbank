package dev.jordanjoseph.backend.dto.contact;

import java.util.UUID;

public record UpdateContactRequest(
        UUID contactId,
        String displayName
) {}
