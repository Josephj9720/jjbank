package dev.jordanjoseph.backend.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityContextAuthenticationFacade implements AuthenticationFacade {

    /** Hides the static call (SecurityContext is static),
     * decouples code by reducing direct dependency on the SecurityContext,
     * improves testability */
    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
