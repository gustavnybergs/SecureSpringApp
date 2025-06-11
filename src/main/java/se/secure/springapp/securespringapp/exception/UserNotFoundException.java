package se.secure.springapp.securespringapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception som kastas när en användare med specificerat ID inte kan hittas.
 * Denna exception ärver från RuntimeException enligt projektuppgiftens krav
 * och hanteras av GlobalExceptionHandler för att returnera HTTP 404 Not Found.
 *
 * Utvecklare 2 (Jawhar) har lagt till @ResponseStatus(HttpStatus.NOT_FOUND) annotation
 * som får Spring att automatiskt returnera 404 Not Found som fallback.
 *
 * Används i situationer där:
 * - En användare försöker accediera en icke-existerande användar-ID
 * - CRUD-operationer refererar till ogiltiga användarreferenser
 * - Admin-funktioner försöker modifiera icke-existerande användare
 *
 * @author Utvecklare 3 (grundläggande), Utvecklare 2 (@ResponseStatus)
 * @version 1.0
 * @since 2024-06-09
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // Jawhars annotation för automatisk 404-status
public class UserNotFoundException extends RuntimeException {

    /**
     * Skapar en UserNotFoundException med standardmeddelande.
     * Använder ett generiskt felmeddelande som inte avslöjar systemdetaljer.
     */
    public UserNotFoundException() {
        super("Användare kunde inte hittas");
    }

    /**
     * Skapar en UserNotFoundException med anpassat felmeddelande.
     * Detta är Jawhars implementation från merge-konflikten.
     *
     * @param message detaljerat felmeddelande som beskriver varför användaren inte hittades
     */
    public UserNotFoundException(String message) {
        super(message); // Jawhars implementation - skickar meddelandet till RuntimeException
    }

    /**
     * Skapar en UserNotFoundException med felmeddelande baserat på användar-ID.
     * Bekvämlighetsmetod för vanliga fall där ett specifikt ID inte kunde hittas.
     *
     * @param userId ID för användaren som inte kunde hittas
     */
    public UserNotFoundException(Long userId) {
        super("Användare med ID " + userId + " kunde inte hittas");
    }

    /**
     * Skapar en UserNotFoundException med felmeddelande och bakomliggande orsak.
     * Användbar när exception orsakas av en annan exception (t.ex. databasfel).
     *
     * @param message felmeddelande som beskriver problemet
     * @param cause bakomliggande exception som orsakade detta fel
     */
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}