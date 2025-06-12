package se.secure.springapp.securespringapp.controller;

import se.secure.springapp.securespringapp.exception.UserNotFoundException;
import se.secure.springapp.securespringapp.exception.ResourceNotFoundException;
import se.secure.springapp.securespringapp.exception.InvalidRoleException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test-controller för att prova våra felhanteringsmeckanismer.
 * Jag skapade denna för User Story #6 (38 enligt github commit) så vi kan
 * testa att GlobalExceptionHandler fångar upp olika typer av
 * fel och returnerar korrekta HTTP-statuskoder.
 *
 * Denna controller kan tas bort när riktiga controllers är implementerade,
 * men den är jättebra för utveckling och testning av felhantering.
 *
 * @author Gustav
 * @version 1.0
 * @since 2025-06-09
 */
@RestController
@RequestMapping("/api/test-errors")
public class ErrorTestController {

    /**
     * Kastar medvetet UserNotFoundException för att testa 404-hantering.
     * Använd detta för att verifiera att GlobalExceptionHandler fångar upp
     * exception och returnerar korrekt 404-svar med vårt ErrorResponse-format.
     *
     * @return ResponseEntity (kommer aldrig returneras p.g.a. exception)
     * @throws UserNotFoundException för att testa felhantering
     */
    @GetMapping("/user-not-found")
    public ResponseEntity<String> testUserNotFound() {
        throw new UserNotFoundException("Användare med ID 123 hittades inte");
    }

    /**
     * Kastar ResourceNotFoundException.
     */
    @GetMapping("/resource-not-found")
    public ResponseEntity<String> testResourceNotFound() {
        throw new ResourceNotFoundException("Resursen du sökte finns inte");
    }

    /**
     * Kastar InvalidRoleException.
     */
    @GetMapping("/invalid-role")
    public ResponseEntity<String> testInvalidRole() {
        throw new InvalidRoleException("Rollen 'SUPER_ADMIN' finns inte i systemet");
    }

    /**
     * Kastar en generisk exception för att testa fallback-hantering.
     */
    @GetMapping("/generic-error")
    public ResponseEntity<String> testGenericError() {
        throw new RuntimeException("Detta är ett oväntat fel");
    }
}