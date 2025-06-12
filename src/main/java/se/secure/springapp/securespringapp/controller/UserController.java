// UserController.java
package se.secure.springapp.securespringapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import se.secure.springapp.securespringapp.service.UserService;

/**
 * Controller för användarspecifika endpoints.
 * Endpoints är tillgängliga för rollerna USER och ADMIN.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    /**
     * Konstruktor för injektion av UserService.
     *
     * @param userService service för användarhantering
     */
    public UserController(UserService userService) {
        this.userService = userService;
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
     * Tar bort den inloggade användarens konto.
     *
     * @param auth autentiseringsobjekt från Spring Security
     * @return HTTP 204 No Content vid lyckad borttagning
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteOwnAccount(Authentication auth) {
        String username = auth.getName();
        userService.deleteUserByUsername(username);
        return ResponseEntity.noContent().build();
    }
}
