package dev.jordanjoseph.backend.service;

import dev.jordanjoseph.backend.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtService {

    //base64 secret
    @Value("${jjb.jwt.secret}")
    private String secretB64;

    @Value("${jjb.jwt.access-mins}")
    private long accessMins;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretB64));
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(accessMins, ChronoUnit.MINUTES)))
                .claim("roles", user.getRoles())
                .signWith(getKey())
                .compact();

    }

    public boolean isValid(String token) {
        try{
            //if this works without exception, then token is valid
            Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /** generic method that returns a value of type T extracted from the Claims object */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /** run after validation */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build().parseSignedClaims(token).getPayload();
    }

}
