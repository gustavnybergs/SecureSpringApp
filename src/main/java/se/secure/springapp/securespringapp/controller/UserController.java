package se.secure.springapp.securespringapp.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import se.secure.springapp.securespringapp.service.UserService;

/**
 * REST-controller för användarspecifika operationer.
 * Endpoints i denna controller ska vara tillgängliga för användare med rollen USER eller ADMIN.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    //  Konstruktor för att injicera UserService
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Hämtar ett användar- eller administratörsspecifikt meddelande.
     *
     * @return Ett ResponseEntity med ett välkomstmeddelande.
     */
    @GetMapping("/hello")
    public ResponseEntity<String> getUserHello() {
        return ResponseEntity.ok("Välkommen, du är inloggad som användare eller admin!");
    }

    /**
     * Tar bort den inloggade användaren från systemet.
     *
     * @param auth Authentication-objektet från Spring Security
     * @return 204 No Content om borttagning lyckas
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteOwnAccount(Authentication auth) {
        String username = auth.getName();
        userService.deleteUserByUsername(username);
        return ResponseEntity.noContent().build();
    }
}
