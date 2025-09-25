package dev.jordanjoseph.backend.config;

import dev.jordanjoseph.backend.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class RefreshTokenLogoutHandler implements LogoutHandler {

    @Autowired
    private AuthService authService;

    @Value("${jjb.allowed-origins}")
    private String allowedOrigins;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String refreshToken = extractRefreshToken(request);
        if(!refreshToken.isBlank()) {
            authService.revokeRefreshToken(refreshToken);
        }
        setResponseHeaders(response);
    }

    private String extractRefreshToken(HttpServletRequest request) {
        //find cookie with name refreshToken and return its value, if not found return empty string
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("refreshToken"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse("");
    }

    private void setResponseHeaders(HttpServletResponse response) {
        //set all required CORS headers explicitly for this logout response
        response.setHeader("Access-Control-Allow-Origin", allowedOrigins);
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }
}
