package se.secure.springapp.securespringapp.exception;

/**
 * Kastas när användare försöker komma åt resurser som inte finns
 * Eller när de inte har rättighet att se en specifik resurs
 * Används för användarspecifika data som dokument, filer etc
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}