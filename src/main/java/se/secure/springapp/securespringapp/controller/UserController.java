package se.secure.springapp.securespringapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-controller för användarspecifika operationer.
 * Endpoints i denna controller ska vara tillgängliga för användare med rollen USER eller ADMIN.
 */
@RestController
@RequestMapping("/api/user") // Alla endpoints här börjar med /api/user
public class UserController {

    /**
     * Hämtar ett användar- eller administratörsspecifikt meddelande.
     * Denna endpoint skyddas av Spring Security för att tillåta både USER- och ADMIN-roller.
     *
     * @return Ett ResponseEntity med ett välkomstmeddelande för användare och admins.
     */
    @GetMapping("/hello") // Enkel endpoint för att testa åtkomst
    public ResponseEntity<String> getUserHello() {
        return ResponseEntity.ok("Välkommen, du är inloggad som användare eller admin!");
    }
}