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
 * Test-controller för att prova våra felmeddelanden
 * Har endpoints som medvetet kastar olika typer av exceptions
 * Används för att verifiera att felhanteringen fungerar korrekt
 * Kan tas bort när riktiga controllers är implementerade
 */
@RestController
@RequestMapping("/api/test-errors")
public class ErrorTestController {

    /**
     * Kastar UserNotFoundException för att testa 404-hantering.
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