package se.secure.springapp.securespringapp.controller;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-controller för administratörsspecifika operationer.
 * Endpoints i denna controller ska endast vara tillgängliga för användare med rollen ADMIN.
 */
@RestController
@RequestMapping("/api/admin") // Alla endpoints här börjar med /api/admin
public class AdminController {

    /**
     * Hämtar ett administratörsspecifikt meddelande.
     * Denna endpoint skyddas av Spring Security för att endast tillåta ADMIN-roller.
     *
     * @return Ett ResponseEntity med ett välkomstmeddelande för admins.
     */
    @GetMapping("/hello") // Enkel endpoint för att testa åtkomst
    public ResponseEntity<String> getAdminHello() {
        return ResponseEntity.ok("Välkommen, du är inloggad som ADMIN!");
    }

    // Här kan du lägga till fler admin-specifika endpoints senare, t.ex. för att hantera användare
    // @DeleteMapping("/users/{id}")
    // public ResponseEntity<String> deleteUser(@PathVariable Long id) { ... }
}