package dev.jordanjoseph.backend.config;

import org.springframework.security.core.Authentication;

/** Abstraction layer */
public interface AuthenticationFacade {
    Authentication getAuthentication();
}
