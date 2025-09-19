package dev.jordanjoseph.backend.controller;

import dev.jordanjoseph.backend.dto.authentication.*;

import dev.jordanjoseph.backend.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResults authResults = authService.login(request);

        //attach cookie to response
        response.addHeader(HttpHeaders.SET_COOKIE, authResults.httpOnlyCookie());

        return ResponseEntity.ok(authResults.authResponse());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@CookieValue("refreshToken") RefreshRequest request, HttpServletResponse response) {
        AuthResults authResults = authService.refresh(request);

        //attach cookie to response
        response.addHeader(HttpHeaders.SET_COOKIE, authResults.httpOnlyCookie());

        return ResponseEntity.ok(authResults.authResponse());
    }

}
