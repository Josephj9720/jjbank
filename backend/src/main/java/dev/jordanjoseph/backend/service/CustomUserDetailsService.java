package dev.jordanjoseph.backend.service;

import dev.jordanjoseph.backend.model.User;
import dev.jordanjoseph.backend.model.UserPrincipal;
import dev.jordanjoseph.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("Not found"));
        return new UserPrincipal(user);
    }

}
