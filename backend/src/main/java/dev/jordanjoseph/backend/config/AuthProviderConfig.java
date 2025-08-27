package dev.jordanjoseph.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AuthProviderConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        return authenticationProvider;

    }

}
