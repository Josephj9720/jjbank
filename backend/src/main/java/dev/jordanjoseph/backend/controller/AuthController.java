package dev.jordanjoseph.backend.controller;

import dev.jordanjoseph.backend.dto.authentication.AuthResponse;
import dev.jordanjoseph.backend.dto.authentication.LoginRequest;
import dev.jordanjoseph.backend.dto.authentication.RefreshRequest;
import dev.jordanjoseph.backend.dto.authentication.RegisterRequest;

import dev.jordanjoseph.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    //need validation for email and password on register: see RegisterRequest
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshRequest request) {
        return authService.refresh(request);
    }

}
