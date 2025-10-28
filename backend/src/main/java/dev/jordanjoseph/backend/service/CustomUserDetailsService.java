package dev.jordanjoseph.backend.service;

import dev.jordanjoseph.backend.model.User;
import dev.jordanjoseph.backend.model.UserPrincipal;
import dev.jordanjoseph.backend.repository.DebitCardRepository;
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

    @Autowired
    private DebitCardRepository debitCardRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(identifier)
                .orElse(
                        debitCardRepository.findByNumber(identifier)
                        .orElseThrow(() -> new UsernameNotFoundException("Not found"))
                        .getOwner()
                );
        return new UserPrincipal(user);
    }

}
