package dev.jordanjoseph.backend.dto.authentication;

//need validation for email and password on register: see AuthService /register
public record RegisterRequest(
        @jakarta.validation.constraints.NotBlank String fullName,
        @jakarta.validation.constraints.Email String email,
        @jakarta.validation.constraints.Size(min = 8) String password
) { }
