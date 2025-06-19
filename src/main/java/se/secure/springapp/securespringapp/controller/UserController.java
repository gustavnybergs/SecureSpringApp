package se.secure.springapp.securespringapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import se.secure.springapp.securespringapp.dto.AppUserDTO;
import se.secure.springapp.securespringapp.model.User;
import se.secure.springapp.securespringapp.model.UserPrincipal;
import se.secure.springapp.securespringapp.service.UserService;
import se.secure.springapp.securespringapp.exception.UserNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import se.secure.springapp.securespringapp.repository.UserRepository;

/**
 * Controller för användarspecifika endpoints.
 * Endpoints är tillgängliga för rollerna USER och ADMIN.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    /**
     * Konstruktor för injektion av UserService.
     *
     * @param userService service för användarhantering
     */
    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**

     * Testendpoint för användar- och adminåtkomst.
     *
     * @return hälsningsmeddelande för inloggad användare eller admin

     */
    @GetMapping("/hello")
    public ResponseEntity<String> getUserHello() {
        return ResponseEntity.ok("Välkommen, du är inloggad som användare eller admin!");
    }

    /**
     * Endpoint för att hämta information om den inloggade användaren.
     *
     * @param auth autentiseringsobjekt från Spring Security
     * @return användarinformation som DTO utan känsliga data
     */
    @GetMapping("/me")
    public ResponseEntity<AppUserDTO> getOwnProfile(Authentication auth) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        Long id = userPrincipal.getUserId(); // Hämta ID från custom UserPrincipal
        User user = userService.getUserById(id);
        return ResponseEntity.ok(new AppUserDTO(user));
    }

    /**
     * Tar bort den inloggade användarens konto.
     *
     * @param auth autentiseringsobjekt från JWT-token
     * @return 200 OK om lyckad radering, 404 om användaren inte finns, 500 vid fel
     */
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteOwnAccount(Authentication auth) {
        try {
            // Hämta email från JWT token
            Jwt jwt = (Jwt) auth.getPrincipal();
            final String email = jwt.getClaimAsString("username");

            // Hitta användaren i databasen
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("Användaren hittades inte"));

            // Radera användaren
            userService.deleteUserById(user.getId());

            return ResponseEntity.ok("Användaren " + email + " har raderats framgångsrikt");

        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body("Användaren hittades inte");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Kunde inte radera användaren: " + e.getMessage());
        }
    }
    /**
     * Visar välkomstmeddelande för autentiserade användare.
     * Denna endpoint kan nås av användare med antingen USER eller ADMIN-roll.
     *
     * @return ResponseEntity som innehåller välkomstmeddelande för användare och administratörer
     */
    @GetMapping  // Detta ger er /api/user
    public ResponseEntity<String> userWelcome() {
        return ResponseEntity.ok("Välkommen users och admins");
    }

}
