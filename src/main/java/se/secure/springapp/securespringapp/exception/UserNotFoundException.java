package se.secure.springapp.securespringapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception som kastas när en användare inte hittas i systemet.
 * Kombinerar Jawhars @ResponseStatus-annotation med Gustavs utökade konstruktorer.
 *
 * Returnerar automatiskt 404 Not Found när denna exception kastas.
 *
 * @author Jawhar
 * @author Gustav
 * @version 2.0 - Kombinerad implementation
 * @since 2025-06-11
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    /**
     * Jawhars ursprungliga konstruktor.
     *
     * @param message felmeddelande
     */
    public UserNotFoundException(String message) {
        super(message);
    }

    /**
     * Gustavs tillägg för bättre felhantering.
     *
     * @param message felmeddelande
     * @param cause orsak till felet
     */
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Konstruktor för användar-ID.
     *
     * @param userId ID för användaren som inte hittades
     */
    public UserNotFoundException(Long userId) {
        super("User with ID " + userId + " not found");
    }

    /**
     * Konstruktor för användarnamn eller anpassat meddelande.
     *
     * @param username användarnamn som inte hittades
     * @param isUsername true om det är användarnamn, false för annat meddelande
     */
    public UserNotFoundException(String username, boolean isUsername) {
        super(isUsername ? "User with username '" + username + "' not found" : username);
    }

    /**
     * Default konstruktor med standardmeddelande.
     */
    public UserNotFoundException() {
        super("User not found");
    }
}