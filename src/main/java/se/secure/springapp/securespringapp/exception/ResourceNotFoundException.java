package se.secure.springapp.securespringapp.exception;

/**
 * Exception som kastas när användare försöker komma åt resurser som inte finns
 * eller som de inte har behörighet att se.
 *
 * Jag skapade denna för User Story #6 (38 enligt github commit) så vi kan hantera
 * 404-fall på ett konsekvent sätt genom hela applikationen. GlobalExceptionHandler
 * fångar denna exception och returnerar lämpligt HTTP 404-svar till klienten.
 *
 * Används för användarspecifika data som dokument, filer, profiler etc.
 * Skillnaden mot UserNotFoundException är att denna är för användarens egna data,
 * medan UserNotFoundException är för själva användarkontot.
 *
 * @author Gustav
 * @version 1.0
 * @since 2025-06-09
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Skapar en ny ResourceNotFoundException med felmeddelande.
     * Använd detta när en specifik resurs inte kan hittas.
     *
     * @param message beskrivning av vilken resurs som inte hittades
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Skapar en ny ResourceNotFoundException med felmeddelande och grundorsak.
     * Använd detta när exception orsakades av en annan exception (t.ex. databas-fel).
     *
     * @param message beskrivning av vad som gick fel
     * @param cause den underliggande exception som orsakade detta fel
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}