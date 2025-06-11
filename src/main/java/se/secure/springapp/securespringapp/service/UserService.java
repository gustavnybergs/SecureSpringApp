package se.secure.springapp.securespringapp.service;

import se.secure.springapp.securespringapp.exception.UserNotFoundException;
import se.secure.springapp.securespringapp.model.User;
import se.secure.springapp.securespringapp.repository.UserRepository;
import se.secure.springapp.securespringapp.model.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

/**
 * Service som hanterar användarrelaterade operationer.
 *
 * Kombinerar Gustav's registreringslogik med Elie's utökade funktionalitet.
 * ÄNDRING: Tog bort UserDetailsService-implementationen för att lösa bean-konflikt.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registrerar ny användare med användarnamn och lösenord (Gustav's version).
     */
    public User registerNewUser(String username, String password, boolean consentGiven) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists!");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setConsentGiven(consentGiven);
        newUser.setRoles(new HashSet<>());
        newUser.addRole(Role.USER);

        return userRepository.save(newUser);
    }

    /**
     * Registrerar ny användare (Elie's förenklad version).
     */
    public User registerUser(String username, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Användarnamnet finns redan");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRoles(new HashSet<>());
        user.addRole(Role.USER);
        user.setConsentGiven(false);

        return userRepository.save(user);
    }

    /**
     * Hittar användare baserat på användarnamn.
     */
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
    }

    /**
     * Tar bort användare baserat på användarnamn.
     */
    public void deleteUserByUsername(String username) {
        User user = findUserByUsername(username);
        userRepository.delete(user);
    }
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Användare med ID " + id + " hittades inte."));
    }
}