package se.secure.springapp.securespringapp.exception;

import se.secure.springapp.securespringapp.exception.UserNotFoundException;
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

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Global felhanterare för REST-controllers.
 * Kombinerar Jawhar's UserNotFoundException-hantering med Utvecklare 3's säkerhetsloggning.
 *
 * Centraliserar felhantering för säkerhet, autentisering och allmänna applikationsfel.
 * Loggar säkerhetshändelser för övervakning och incident response.
 *
 * @author Jawhar (UserNotFoundException), Utvecklare 3 (säkerhetsloggning och utökad felhantering)
 * @version 2.0 - Kombinerad implementation
 * @since 2025-06-11
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY");

    /**
     * Hanterar UserNotFoundException från Jawhar's implementation.
     * Returnerar 404 Not Found när användare inte hittas i systemet.
     *
     * @param ex Undantaget som kastades
     * @param request WebRequest-objektet med begäran-information
     * @return ResponseEntity med felmeddelande och HTTP-status 404
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(
            UserNotFoundException ex, WebRequest request) {

        logger.warn("User not found: {} - Request: {}", ex.getMessage(), request.getDescription(false));

        Map<String, Object> errorResponse = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.NOT_FOUND.value(),
                "error", "User Not Found",
                "message", ex.getMessage(),
                "path", request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Hanterar autentiseringsfel (felaktiga användaruppgifter).
     * Loggar säkerhetshändelser för potentiella intrångsförsök.
     *
     * @param ex AuthenticationException från inloggningsförsök
     * @param request WebRequest med begäran-information
     * @return ResponseEntity med 401 Unauthorized
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {

        // Säkerhetsloggning för misslyckade inloggningsförsök
        securityLogger.warn("Authentication failed - IP: {}, Request: {}, Reason: {}",
                getClientIP(request), request.getDescription(false), ex.getMessage());

        Map<String, Object> errorResponse = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.UNAUTHORIZED.value(),
                "error", "Authentication Failed",
                "message", "Invalid username or password",
                "path", request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Hanterar åtkomstförbud (användare har inte tillräckliga rättigheter).
     * Loggar säkerhetshändelser för obehöriga åtkomstförsök.
     *
     * @param ex AccessDeniedException från rollbaserad säkerhet
     * @param request WebRequest med begäran-information
     * @return ResponseEntity med 403 Forbidden
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {

        // Säkerhetsloggning för obehöriga åtkomstförsök
        securityLogger.warn("Access denied - IP: {}, Request: {}, Reason: {}",
                getClientIP(request), request.getDescription(false), ex.getMessage());

        Map<String, Object> errorResponse = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.FORBIDDEN.value(),
                "error", "Access Denied",
                "message", "You don't have permission to access this resource",
                "path", request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Hanterar felaktiga användaruppgifter specifikt (undermängd av AuthenticationException).
     *
     * @param ex BadCredentialsException från inloggning
     * @param request WebRequest med begäran-information
     * @return ResponseEntity med 401 Unauthorized
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsException(
            BadCredentialsException ex, WebRequest request) {

        securityLogger.warn("Bad credentials attempt - IP: {}, Request: {}",
                getClientIP(request), request.getDescription(false));

        Map<String, Object> errorResponse = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.UNAUTHORIZED.value(),
                "error", "Invalid Credentials",
                "message", "Username or password is incorrect",
                "path", request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Allmän felhanterare för oväntade serverfel.
     * Loggar detaljerad information för debugging utan att exponera känslig information.
     *
     * @param ex Exception som inte fångats av andra handlers
     * @param request WebRequest med begäran-information
     * @return ResponseEntity med 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, WebRequest request) {

        // Logga fullständig stack trace för debugging
        logger.error("Unexpected error occurred - Request: {}", request.getDescription(false), ex);

        // Returnera generiskt felmeddelande utan känslig information
        Map<String, Object> errorResponse = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "error", "Internal Server Error",
                "message", "An unexpected error occurred. Please try again later.",
                "path", request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Hanterar IllegalArgumentException för validerings-fel.
     *
     * @param ex IllegalArgumentException från validering
     * @param request WebRequest med begäran-information
     * @return ResponseEntity med 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        logger.warn("Invalid argument provided - Request: {}, Error: {}",
                request.getDescription(false), ex.getMessage());

        Map<String, Object> errorResponse = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Bad Request",
                "message", ex.getMessage(),
                "path", request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Extraherar klient-IP från WebRequest för säkerhetsloggning.
     * Kollar först X-Forwarded-For header för proxy-situationer.
     *
     * @param request WebRequest att extrahera IP från
     * @return Klient-IP som sträng
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

        // Fallback till remote address från request
        return request.getRemoteAddress();
    }
}