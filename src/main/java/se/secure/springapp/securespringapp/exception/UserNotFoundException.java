package se.secure.springapp.securespringapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Anpassat undantag som kastas när en användare inte hittas.
 * Används för att signalera ett 404 Not Found HTTP-svar.
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // Denna annotation får Spring att returnera 404 Not Found
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message); // Skickar meddelandet till RuntimeException
    }
}