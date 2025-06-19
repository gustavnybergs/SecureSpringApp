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
 * Kombinerar Gustavs registreringslogik med Elies utökade funktionalitet och Jawhars metoder för registrering
 * ÄNDRING: Tog bort UserDetailsService-implementationen för att lösa bean-konflikt.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityEventLogger securityEventLogger; // * Logger för säkerhetshändelser

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, SecurityEventLogger securityEventLogger) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityEventLogger = securityEventLogger;
    }

    /**
     * Registrerar ny användare
     * Kombinerar email och username-validering med säker lösenordshantering.
     * Loggar registreringen
     *
     * @param registerRequest registreringsdata från frontend
     * @return sparad användare
     * @throws IllegalArgumentException om användare redan finns eller samtycke saknas
     */
    public User registerUser(RegisterRequest registerRequest) {
        // Kontrollera att samtycke givits (GDPR-krav)
        if (!registerRequest.isConsentGiven()) {
            throw new IllegalArgumentException("Samtycke till datalagring krävs för registrering");
        }

        // Kontrollera dubbletter
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Användarnamnet är redan taget");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email-adressen är redan registrerad");
        }

        // Skapa och konfigurera ny användare
        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setFullName(registerRequest.getFullName());
        newUser.addRole(Role.USER);
        newUser.setConsentGiven(true); // Samtycke bekräftat

        // Spara användare
        User savedUser = userRepository.save(newUser);

        // Logga registrering för säkerhetsrevision
        securityEventLogger.logUserRegistration(savedUser.getEmail());

        return savedUser;
    }

    /**
     * Hittar användare baserat på användarnamn.
     *
     * @param username användarnamn att söka efter
     * @return användare om den hittas
     * @throws IllegalArgumentException om användaren inte finns
     */
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
    }

    /**
     * Tar bort användare baserat på användarnamn.
     *
     * @param username användarnamn för användaren som ska tas bort
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


}