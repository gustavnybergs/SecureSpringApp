package se.secure.springapp.securespringapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Anpassat undantag för när en användare inte hittas i systemet.
 * Kombinerar Jawhar's @ResponseStatus-annotation med Utvecklare 3's utökade konstruktorer.
 *
 * Används för att signalera 404 Not Found HTTP-svar när användaroperationer misslyckas
 * på grund av att användaren inte existerar i databasen.
 *
 * @author Jawhar (@ResponseStatus-annotation), Utvecklare 3 (utökade konstruktorer)
 * @version 2.0 - Kombinerad implementation
 * @since 2025-06-11
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // Jawhar's annotation - returnerar automatiskt 404 Not Found
public class UserNotFoundException extends RuntimeException {

    /**
     * Standard konstruktor med anpassat felmeddelande.
     * Jawhar's ursprungliga implementation.
     *
     * @param message Beskrivande meddelande om vad som gick fel
     */
    public UserNotFoundException(String message) {
        super(message);
    }

    /**
     * Konstruktor med meddelande och orsakskedja för bättre felsökning.
     * Utvecklare 3's tillägg för mer detaljerad felhantering.
     *
     * @param message Beskrivande meddelande om vad som gick fel
     * @param cause Underliggande orsak till felet (t.ex. databasfel)
     */
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Konstruktor med fördefinierat meddelande baserat på användar-ID.
     * Utvecklare 3's bekvämlighetsmetod för vanliga fall.
     *
     * @param userId ID för användaren som inte kunde hittas
     */
    public UserNotFoundException(Long userId) {
        super("User with ID " + userId + " not found");
    }

    /**
     * Konstruktor med fördefinierat meddelande baserat på användarnamn.
     * Utvecklare 3's bekvämlighetsmetod för autentiseringsfel.
     *
     * @param username Användarnamnet som inte kunde hittas
     */
    public UserNotFoundException(String username, boolean isUsername) {
        super(isUsername ? "User with username '" + username + "' not found" : username);
    }

    /**
     * Standard konstruktor utan meddelande.
     * Utvecklare 3's tillägg för situationer där meddelandet sätts senare.
     */
    public UserNotFoundException() {
        super("User not found");
    }
}