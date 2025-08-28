package dev.jordanjoseph.backend.dto;

//need validation for email and password on register: see AuthService /register
public record RegisterRequest(
        @jakarta.validation.constraints.Email String email,
        @jakarta.validation.constraints.Size(min = 8) String password
) { }
