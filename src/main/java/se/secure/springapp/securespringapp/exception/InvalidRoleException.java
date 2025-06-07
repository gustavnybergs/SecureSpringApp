package se.secure.springapp.securespringapp.exception;

/**
 * Kastas när någon försöker använda en roll som inte är giltig
 * Tex när admin försöker tilldela en roll som inte finns
 * Eller när systemet hittar korrupt rolldata
 */
public class InvalidRoleException extends RuntimeException {

    public InvalidRoleException(String message) {
        super(message);
    }

    public InvalidRoleException(String message, Throwable cause) {
        super(message, cause);
    }
}