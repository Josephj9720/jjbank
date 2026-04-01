package dev.jordanjoseph.backend.dto.contact;

import java.util.UUID;

public record DeleteContactRequest(
        UUID contactId
) {}
