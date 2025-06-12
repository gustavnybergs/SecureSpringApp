package se.secure.springapp.securespringapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import se.secure.springapp.securespringapp.dto.AppUserDTO;
import se.secure.springapp.securespringapp.model.User;
import se.secure.springapp.securespringapp.model.UserPrincipal;
import se.secure.springapp.securespringapp.service.UserService;

/**
 * REST-controller för användarspecifika operationer.
 * Endpoints i denna controller ska vara tillgängliga för användare med rollen USER eller ADMIN.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    // Konstruktorinjektion av UserService
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Test-endpoint för att verifiera att användaren är inloggad.
     */
    @GetMapping("/hello")
    public ResponseEntity<String> getUserHello() {
        return ResponseEntity.ok("Välkommen, du är inloggad som användare eller admin!");
    }

    /**
     * Endpoint för att hämta information om den inloggade användaren.
     */
    @GetMapping("/me")
    public ResponseEntity<AppUserDTO> getOwnProfile(Authentication auth) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        Long id = userPrincipal.getUserId(); // Hämta ID från custom UserPrincipal
        User user = userService.getUserById(id);
        return ResponseEntity.ok(new AppUserDTO(user));
    }

    /**
     * Endpoint för att ta bort sitt eget konto.
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteOwnAccount(Authentication auth) {
        String username = auth.getName();
        userService.deleteUserByUsername(username);
        return ResponseEntity.noContent().build();
    }
}
