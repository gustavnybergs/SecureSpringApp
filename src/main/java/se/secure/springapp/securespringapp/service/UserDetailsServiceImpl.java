package se.secure.springapp.securespringapp.service;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import se.secure.springapp.securespringapp.model.User;
import se.secure.springapp.securespringapp.model.UserPrincipal;
import se.secure.springapp.securespringapp.repository.UserRepository;

@Service
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Laddar användare baserat på email (för login).
     *
     * @param email användarens email
     * @return UserDetails som används av Spring Security
     * @throws UsernameNotFoundException om användare inte hittas
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Användare hittades inte med email: " + email));
        return UserPrincipal.create(user);
    }

    /**
     * Laddar användare baserat på id, används främst vid JWT-verifiering.
     *
     * OBS! Detta är en egen metod, finns inte i UserDetailsService interfacet.
     *
     * @param id användarens id
     * @return UserDetails som används av Spring Security
     * @throws UsernameNotFoundException om användare inte hittas
     */
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Användare hittades inte med ID: " + id));
        return UserPrincipal.create(user);
    }
}
