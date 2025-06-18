package se.secure.springapp.securespringapp.exception;

import org.springframework.web.bind.MethodArgumentNotValidException;
import se.secure.springapp.securespringapp.exception.UserNotFoundException;
import se.secure.springapp.securespringapp.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Global felhanterare för REST-controllers.
 * Kombinerar Jawhars UserNotFoundException-hantering med Gustavs säkerhetsloggning och ErrorResponse.
 *
 * Centraliserar felhantering för säkerhet, autentisering och allmänna applikationsfel.
 * Loggar säkerhetshändelser för övervakning och incident response.
 * Använder nu Gustavs ErrorResponse för konsekvent felformat.
 *
 * @author Jawhar (UserNotFoundException), Gustav (säkerhetsloggning, ErrorResponse och utökad felhantering)
 * @version 2.1 - Kombinerad implementation med ErrorResponse integration
 * @since 2025-06-11
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY");

    /**
     * Hanterar UserNotFoundException från Jawhars implementation.
     * Jag behåller detaljerad loggning internt men returnerar bara felmeddelandet till klienten.
     *
     * @param ex undantaget som kastades när användaren inte hittades
     * @param request information om den inkommande HTTP-förfrågan
     * @return ErrorResponse med 404-status
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(
            UserNotFoundException ex, WebRequest request) {

        logger.warn("User not found: {} - Request: {}", ex.getMessage(), request.getDescription(false));

        // Loggar detaljerna för debugging
        logger.debug("Detailed error response for user not found: status=404, path={}",
                request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Hanterar autentiseringsfel (felaktiga användaruppgifter).
     * Loggar säkerhetshändelser för potentiella intrångsförsök.
     * Använder nu ErrorResponse för konsekvent format.
     *
     * @param ex AuthenticationException från inloggningsförsök
     * @param request WebRequest med begäran-information
     * @return ResponseEntity med ErrorResponse och 401 Unauthorized
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {

        // Säkerhetsloggning för misslyckade inloggningsförsök
        securityLogger.warn("Authentication failed - IP: {}, Request: {}, Reason: {}",
                getClientIP(request), request.getDescription(false), ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Authentication Failed",
                "Invalid username or password",
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Hanterar åtkomstförbud (användare har inte tillräckliga rättigheter).
     * Loggar säkerhetshändelser för obehöriga åtkomstförsök.
     * Använder nu ErrorResponse för konsekvent format.
     *
     * @param ex AccessDeniedException från rollbaserad säkerhet
     * @param request WebRequest med begäran-information
     * @return ResponseEntity med ErrorResponse och 403 Forbidden
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {

        // Säkerhetsloggning för obehöriga åtkomstförsök
        securityLogger.warn("Access denied - IP: {}, Request: {}, Reason: {}",
                getClientIP(request), request.getDescription(false), ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Access Denied",
                "You don't have permission to access this resource",
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Hanterar felaktiga användaruppgifter specifikt (undermängd av AuthenticationException).
     * Använder nu ErrorResponse för konsekvent format.
     *
     * @param ex BadCredentialsException från inloggning
     * @param request WebRequest med begäran-information
     * @return ResponseEntity med ErrorResponse och 401 Unauthorized
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex, WebRequest request) {

        securityLogger.warn("Bad credentials attempt - IP: {}, Request: {}",
                getClientIP(request), request.getDescription(false));

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid Credentials",
                "Username or password is incorrect",
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Allmän felhanterare för oväntade serverfel.
     * Loggar detaljerad information för debugging utan att exponera känslig information.
     * Använder nu ErrorResponse för konsekvent format.
     *
     * @param ex Exception som inte fångats av andra handlers
     * @param request WebRequest med begäran-information
     * @return ResponseEntity med ErrorResponse och 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {

        // Logga fullständig stack trace för debugging
        logger.error("Unexpected error occurred - Request: {}", request.getDescription(false), ex);

        // Returnera generiskt felmeddelande utan känslig information
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Hanterar IllegalArgumentException för validerings-fel.
     * Använder nu ErrorResponse för konsekvent format.
     *
     * @param ex IllegalArgumentException från validering
     * @param request WebRequest med begäran-information
     * @return ResponseEntity med ErrorResponse och 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        logger.warn("Invalid argument provided - Request: {}, Error: {}",
                request.getDescription(false), ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Extraherar klient-IP från WebRequest för säkerhetsloggning.
     * Kollar först X-Forwarded-For header för proxy-situationer.
     *
     * @param request WebRequest att extrahera IP från
     * @return Klient-IP som sträng eller "unknown" om det inte kan bestämmas
     */
    private String getClientIP(WebRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }

        // Fallback - WebRequest har ingen direkt metod för remote address
        return "unknown";
    }

    /**
     * Hanterar Bean Validation-fel från @Valid annotationer.
     * Konverterar MethodArgumentNotValidException till 400 Bad Request.
     *
     * @param ex MethodArgumentNotValidException från Spring validation
     * @param request WebRequest med begäran-information
     * @return ResponseEntity med ErrorResponse och 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {

        // Samla alla valideringsfel
        StringBuilder errorMessage = new StringBuilder("Valideringsfel: ");
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errorMessage.append(error.getField())
                        .append(" - ")
                        .append(error.getDefaultMessage())
                        .append("; ")
        );

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                errorMessage.toString(),
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}