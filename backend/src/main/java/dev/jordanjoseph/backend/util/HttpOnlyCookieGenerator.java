package dev.jordanjoseph.backend.util;

import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

@Component
public class HttpOnlyCookieGenerator {

    public ResponseCookie generate(String name, String token, long duration, TemporalUnit temporalUnit) {
        return ResponseCookie.from(name, token)
                .httpOnly(true)
                .secure(false) //set to true in production
                .path("/api")
                .maxAge(Duration.ZERO.plus(duration, temporalUnit))
                .sameSite(SameSiteCookies.STRICT.toString())
                .build();
    }

}
