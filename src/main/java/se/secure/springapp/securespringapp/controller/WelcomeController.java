package se.secure.springapp.securespringapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller för hantering av applikationens förstasida.
 * Tillhandahåller välkomstfunktionalitet för inloggade användare.
 *
 * @author Gustav
 * @version 1.0
 */
@RestController
public class WelcomeController {

    /**
     * Visar förstasidan för inloggade användare.
     * Denna endpoint fungerar som huvudsida efter inloggning.
     *
     * @return ResponseEntity som innehåller välkomstmeddelande som bekräftar att användaren är inloggad
     */
    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Du är inloggad");
    }
}
