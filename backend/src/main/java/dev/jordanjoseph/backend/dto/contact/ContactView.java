package dev.jordanjoseph.backend.dto.contact;

import java.util.UUID;

public record ContactView(
        UUID id,
        String displayName,
        @jakarta.validation.constraints.Email String email
) {}
