package dev.jordanjoseph.backend.service;

import dev.jordanjoseph.backend.dto.AuthResponse;
import dev.jordanjoseph.backend.dto.LoginRequest;
import dev.jordanjoseph.backend.dto.RefreshRequest;
import dev.jordanjoseph.backend.dto.RegisterRequest;
import dev.jordanjoseph.backend.model.RefreshToken;
import dev.jordanjoseph.backend.model.User;
import dev.jordanjoseph.backend.repository.RefreshTokenRepository;
import dev.jordanjoseph.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository users;

    @Autowired
    private RefreshTokenRepository refreshTokens;

    @Autowired
    private AccountService accountService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder encoder;

    @Value("${jjb.jwt.refresh-days}")
    private long refreshDays;

    @Transactional
    public void register(RegisterRequest request) {
        if(users.findByEmail(request.email()).isPresent()) throw new IllegalArgumentException("Email already used!");
        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPasswordHash(encoder.encode(request.password()));
        user.getRoles().add("ROLE_USER");
        users.save(user);

        //create default account
        accountService.createForUser(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = users.findByEmail(request.email()).orElseThrow(() -> new BadCredentialsException("Invalid credentials!"));
        if(!encoder.matches(request.password(), user.getPasswordHash())) throw new BadCredentialsException("Invalid credentials!");

        String accessToken = jwtService.generateToken(user);
        //one refresh at a time: revoke old tokens
        refreshTokens.revokeAllForUser(user.getId());
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString()); //opaque refresh token => not JWT
        refreshToken.setExpiresAt(Instant.now().plus(refreshDays, ChronoUnit.DAYS));
        refreshTokens.save(refreshToken);

        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken refreshToken = refreshTokens.findByTokenAndRevokedFalse(request.refreshToken())
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token!"));
        if(refreshToken.getExpiresAt().isBefore(Instant.now()))  throw new BadCredentialsException("Expired refresh token!");

        User user = refreshToken.getUser();
        String newAccessToken = jwtService.generateToken(user);
        //rotate refresh token = issue a new one when it is used to get a new access (jwt) token
        refreshToken.setRevoked(true);
        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setUser(user);
        newRefreshToken.setToken(UUID.randomUUID().toString());
        newRefreshToken.setExpiresAt(Instant.now().plus(refreshDays, ChronoUnit.DAYS));
        refreshTokens.save(newRefreshToken);

        return new AuthResponse(newAccessToken, newRefreshToken.getToken());
    }
}
