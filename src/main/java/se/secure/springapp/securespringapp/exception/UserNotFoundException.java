package se.secure.springapp.securespringapp.exception;

/**
 * Exception för när vi inte hittar en användare i databasen
 * Kastas oftast när vi söker på ID eller användarnamn som inte existerar
 * Ärver från RuntimeException så vi slipper checked exceptions
 */

public class UserNotFoundException extends RuntimeException {

    /**
     * Standard konstruktor med bara felmeddelande.
     */
    public UserNotFoundException(String message) {
        super(message);
    }

    /**
     * Konstruktor med både meddelande och ursprunglig orsak.
     * Bra när vi wrappar andra exceptions.
     */
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}