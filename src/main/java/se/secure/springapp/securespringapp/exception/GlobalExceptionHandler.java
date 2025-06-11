package se.secure.springapp.securespringapp.exception;

import se.secure.springapp.securespringapp.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Global undantagshanterare som fångar upp alla fel i applikationen.
 * Jag skapade denna för User Story #6 (38 på github) eftersom vi behövde konsekvent felhantering
 * och standardiserade felmeddelanden för hela REST API:et.
 *
 * Utvecklare 2 (Jawhar) har implementerat UserNotFoundException-hantering
 * med @ControllerAdvice som jag nu har integrerat med min säkerhetsloggning.
 *
 * Utan detta skulle olika fel se olika ut och användare skulle få tekniska
 * felmeddelanden som kan avslöja känslig systeminformation. Nu får alla
 * fel samma format och vi kan logga säkerhetshändelser ordentligt.
 *
 * @author Utvecklare 3 (säkerhetslogik), Utvecklare 2 (UserNotFoundException)
 * @version 1.0
 * @since 2025-06-09
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Hanterar UserNotFoundException och returnerar 404-svar.
     * Enligt VG-kraven ska detta returnera ResponseEntity med 404-status.
     * Kombinerar Jawhars ursprungliga implementation med säkerhetsloggning.
     *
     * @param ex UserNotFoundException som kastats
     * @param request HTTP-request för kontext och loggning (om tillgänglig)
     * @return ResponseEntity med ErrorResponse och 404-status
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
            UserNotFoundException ex,
            HttpServletRequest request) {

        String requestPath = request != null ? request.getRequestURI() : "unknown";
        logger.warn("UserNotFoundException på endpoint {}: {}", requestPath, ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Fallback-metod för UserNotFoundException utan HttpServletRequest.
     * Behövs för bakåtkompatibilitet med Jawhars ursprungliga WebRequest-implementation.
     *
     * @param ex UserNotFoundException som kastats
     * @param request WebRequest-objektet från Spring
     * @return ResponseEntity med felmeddelande och HTTP-status 404
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundExceptionLegacy(
            UserNotFoundException ex,
            WebRequest request) {

        logger.warn("UserNotFoundException (legacy): {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Hanterar AccessDeniedException när användare saknar behörighet.
     * Detta händer när någon försöker komma åt admin-endpoints utan ADMIN-roll
     * eller andra skyddade resurser de inte har tillgång till.
     *
     * @param ex AccessDeniedException från Spring Security
     * @param request HTTP-request för loggning av säkerhetshändelser
     * @return ResponseEntity med ErrorResponse och 403-status
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {

        String requestPath = request.getRequestURI();
        String clientIp = request.getRemoteAddr();

        logger.warn("SECURITY - Åtkomst nekad på endpoint {} från IP {}: {}",
                requestPath, clientIp, ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.FORBIDDEN.value());
        errorResponse.setMessage("Du saknar behörighet för att komma åt denna resurs");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * Hanterar autentiseringsfel från Spring Security.
     * Detta händer när JWT-tokens är ogiltiga, utgångna eller saknas helt.
     * Viktigt att logga för säkerhetsövervakning.
     *
     * @param ex AuthenticationException från Spring Security
     * @param request HTTP-request för säkerhetsloggning
     * @return ResponseEntity med ErrorResponse och 401-status
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {

        String clientIp = request.getRemoteAddr();
        String requestPath = request.getRequestURI();

        logger.warn("SECURITY - Autentiseringsfel från IP {} på endpoint {}: {}",
                clientIp, requestPath, ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setMessage("Autentisering krävs. Ange en giltig JWT-token.");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Hanterar validationsfel från Spring Boot Validation.
     * Detta händer när inkommande data inte uppfyller valideringskraven,
     * t.ex. vid användarregistrering med ogiltigt lösenord.
     *
     * @param ex MethodArgumentNotValidException från Spring Validation
     * @param request HTTP-request för kontext
     * @return ResponseEntity med ErrorResponse och 400-status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        StringBuilder errorMessage = new StringBuilder("Valideringsfel: ");
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errorMessage.append(error.getField())
                        .append(" - ")
                        .append(error.getDefaultMessage())
                        .append("; ")
        );

        logger.info("Valideringsfel på endpoint {}: {}", request.getRequestURI(), errorMessage);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage(errorMessage.toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Hanterar IllegalArgumentException från våra egna valideringar.
     * Detta händer t.ex. när SecurityEventLogger får null-värden eller
     * andra metoder får ogiltiga parametrar.
     *
     * @param ex IllegalArgumentException som kastats
     * @param request HTTP-request för kontext
     * @return ResponseEntity med ErrorResponse och 400-status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        logger.warn("IllegalArgumentException på endpoint {}: {}",
                request.getRequestURI(), ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage("Ogiltiga parametrar: " + ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Hanterar allmänna RuntimeException som inte fångas av andra handlers.
     * Detta är en säkerhetsnät för oväntade fel och säkerställer att ingen
     * känslig systeminformation exponeras för klienter.
     *
     * @param ex RuntimeException som kastats
     * @param request HTTP-request för loggning
     * @return ResponseEntity med ErrorResponse och 500-status
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {

        String requestPath = request.getRequestURI();
        String clientIp = request.getRemoteAddr();

        logger.error("Oväntat fel på endpoint {} från IP {}: {}",
                requestPath, clientIp, ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setMessage("Ett internt fel har inträffat. Kontakta systemadministratören.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}