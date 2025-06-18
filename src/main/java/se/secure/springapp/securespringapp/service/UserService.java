package se.secure.springapp.securespringapp.service;

import se.secure.springapp.securespringapp.dto.RegisterRequest;
import se.secure.springapp.securespringapp.exception.UserNotFoundException;
import se.secure.springapp.securespringapp.model.User;
import se.secure.springapp.securespringapp.repository.UserRepository;
import se.secure.springapp.securespringapp.model.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import se.secure.springapp.securespringapp.service.SecurityEventLogger;

import java.util.HashSet;

/**
 * Service som hanterar användarrelaterade operationer.
 *
 * Kombinerar Gustavs registreringslogik med Elies utökade funktionalitet.
 * ÄNDRING: Tog bort UserDetailsService-implementationen för att lösa bean-konflikt.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityEventLogger securityEventLogger; // * Logger för säkerhetshändelser enligt VG-krav.

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, SecurityEventLogger securityEventLogger) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityEventLogger = securityEventLogger;
    }

    /**
     * Registrerar ny användare med användarnamn och lösenord (Gustavs version).
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
     * Registrerar ny användare Elies version.
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

    /**
     * Tar bort en användare baserat på användar-ID.
     * Används när en användare vill ta bort sitt eget konto via JWT-autentisering.
     * Loggar borttagningen
     *
     * @param userId ID för användaren som ska tas bort
     * @throws UserNotFoundException om användaren inte finns
     */
    public void deleteUserById(Long userId) {
        // Hämta användare först för att kunna logga email
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Användare med ID " + userId + " hittades inte."));

        //  Logga borttagning
        securityEventLogger.logUserDeletion(
                userToDelete.getEmail(),
                "SYSTEM" // eller "SELF" för självborttagning
        );

        // Ta bort användaren
        userRepository.deleteById(userId);
    }
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Användare med ID " + id + " hittades inte."));
    }

    /**
     * Registrerar ny användare
     * Kombinerar email och username-validering med säker lösenordshantering.
     * Loggar registreringen
     *
     * @param registerRequest registreringsdata från frontend
     * @return sparad användare
     * @throws IllegalArgumentException om användare redan finns
     */
    public User registerUser(RegisterRequest registerRequest) {
        // Kontrollera dubbletter
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Användarnamnet är redan taget");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email-adressen är redan registrerad");
        }

        // Skapa och spara användare
        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setFullName(registerRequest.getFullName());
        newUser.addRole(Role.USER);
        newUser.setConsentGiven(false);

        User savedUser = userRepository.save(newUser);

        // Loggar registrering
        securityEventLogger.logUserRegistration(savedUser.getEmail());
        return savedUser;
    }
}