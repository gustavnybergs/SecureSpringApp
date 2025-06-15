package se.secure.springapp.securespringapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import se.secure.springapp.securespringapp.dto.AppUserDTO;
import se.secure.springapp.securespringapp.model.User;
import se.secure.springapp.securespringapp.model.UserPrincipal;
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
     * @param auth autentiseringsobjekt från Spring Security
     * @return HTTP 204 No Content vid lyckad borttagning
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteOwnAccount(Authentication auth) {
        // Hämta user ID från JWT token (sub claim)
        String userId = auth.getName();  // Detta är "sub" från JWT (user ID)

        // Konvertera till Long och använd deleteById
        Long userIdLong = Long.parseLong(userId);
        userService.deleteUserById(userIdLong);

        return ResponseEntity.noContent().build();
    }
}
