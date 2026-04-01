package dev.jordanjoseph.backend.dto.email;

public record EmailContent(
        String subject,
        String htmlContent,
        String textContent
) {}
