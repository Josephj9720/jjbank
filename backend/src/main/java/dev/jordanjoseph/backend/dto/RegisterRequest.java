package dev.jordanjoseph.backend.dto;

//need validation for email and password on register: see AuthService /register
public record RegisterRequest(String email, String password) {
}
