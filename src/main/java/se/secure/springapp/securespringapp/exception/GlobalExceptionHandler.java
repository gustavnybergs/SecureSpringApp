package se.secure.springapp.securespringapp.exception;


import se.secure.springapp.securespringapp.exception.UserNotFoundException; // import för DIN exception!
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Global felhanterare för REST-controllers.
 * Använder @ControllerAdvice för att centralisera felhantering.
 */
@ControllerAdvice // Markerar denna klass som en global felhanterare för alla controllers
public class GlobalExceptionHandler {

    /**
     * Hanterar UserNotFoundException och returnerar en 404 Not Found-status.
     *
     * @param ex      Undantaget som kastades.
     * @param request WebRequest-objektet.
     * @return ResponseEntity med felmeddelande och HTTP-status 404.
     */
    @ExceptionHandler(UserNotFoundException.class) // Anger att denna metod hanterar UserNotFoundException
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // Här kan du lägga till fler @ExceptionHandler-metoder för andra typer av fel, t.ex.
    // @ExceptionHandler(IllegalArgumentException.class)
    // public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
    //    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    // }
}