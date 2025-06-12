package se.secure.springapp.securespringapp.exception;

/**
 * Exception som kastas när en ogiltig användarroll specificeras.
 * Denna exception ärver från RuntimeException och hanteras av
 * GlobalExceptionHandler för att returnera HTTP 400 Bad Request.
 *
 * Används när:
 * - Ogiltig roll anges vid användarregistrering
 * - Rolluppdateringar försöker sätta icke-existerande roller
 * - Validering av rollbaserad åtkomst misslyckas
 *
 * @author Gustav
 * @version 1.0
 * @since 2024-06-09
 */
public class InvalidRoleException extends RuntimeException {

    /**
     * Skapar en InvalidRoleException med standardmeddelande.
     * Använder ett generiskt meddelande för ogiltiga roller.
     */
    public InvalidRoleException() {
        super("Ogiltig användarroll specificerad");
    }

    /**
     * Skapar en InvalidRoleException med anpassat felmeddelande.
     * Tillåter kontextspecifika felmeddelanden för olika rollvalideringsfel.
     *
     * @param message detaljerat felmeddelande som beskriver rollproblemet
     */
    public InvalidRoleException(String message) {
        super(message);
    }

    /**
     * Skapar en InvalidRoleException med information om den ogiltiga rollen.
     * Metod som inkluderar den specifika roll som var ogiltig.
     *
     * @param invalidRole den roll som inte kunde valideras
     */
    public InvalidRoleException(String invalidRole, String context) {
        super("Ogiltig roll '" + invalidRole + "' i kontext: " + context);
    }

    /**
     * Skapar en InvalidRoleException med felmeddelande och bakomliggande orsak.
     * Användbar när rollvalidering misslyckas på grund av systemfel.
     *
     * @param message felmeddelande som beskriver rollproblemet
     * @param cause bakomliggande exception som orsakade valideringsfelet
     */
    public InvalidRoleException(String message, Throwable cause) {
        super(message, cause);
    }
}