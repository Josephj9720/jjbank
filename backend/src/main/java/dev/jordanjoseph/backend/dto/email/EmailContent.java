package dev.jordanjoseph.backend.dto.email;

public record EmailContent(
        String htmlContent,
        String textContent
) {}
