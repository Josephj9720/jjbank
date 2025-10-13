package dev.jordanjoseph.backend.dto.account;

import java.util.UUID;

public record CreateRequest(
        UUID userId,
        @jakarta.validation.constraints.NotBlank String accountType
) { }
