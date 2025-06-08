package se.secure.springapp.securespringapp.exception;

import se.secure.springapp.securespringapp.dto.ErrorResponse;
import se.secure.springapp.securespringapp.service.SecurityEventLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fångar upp alla exceptions i hela applikationen
 * Omvandlar dem till snygga JSON-felmeddelanden
 *
 * Placerad centralt så alla controllers får samma felhantering automatiskt
 * Hanterar både våra egna exceptions och Spring Security fel
 *
 * Utvecklare 2: Säkerhetsloggning tillagd enligt User Story #37
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final SecurityEventLogger securityEventLogger;

    @Autowired
    public GlobalExceptionHandler(SecurityEventLogger securityEventLogger) {
        this.securityEventLogger = securityEventLogger;
    }

    /**
     * Hanterar vårt eget UserNotFoundException.
     * Returnerar 404 eftersom användaren inte hittades.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
            UserNotFoundException ex, WebRequest request) {

        // Logga som potentiell säkerhetshändelse
        try {
            String username = getCurrentUsername();
            String ipAddress = getClientIpAddress();
            securityEventLogger.logSuspiciousActivity(
                    "Attempted operation on non-existent user: " + ex.getMessage(),
                    username,
                    ipAddress
            );
        } catch (Exception e) {
            // Ignorera fel i loggning
        }

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "User Not Found",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Hanterar Spring Security's UsernameNotFoundException.
     * Också 404 men med mer generiskt meddelande av säkerhetsskäl.
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(
            UsernameNotFoundException ex, WebRequest request) {

        // Logga som säkerhetshändelse - misslyckad autentisering
        try {
            String ipAddress = getClientIpAddress();
            securityEventLogger.logFailedLogin("UNKNOWN_USER", ipAddress, "Invalid credentials");
        } catch (Exception e) {
            // Ignorera fel i loggning
        }

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Authentication Error",
                "Ogiltiga inloggningsuppgifter", // Inte för specifikt
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Hanterar när användare försöker komma åt resurser de inte får se.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        // Logga som potentiell säkerhetshändelse om användaren är inloggad
        try {
            String username = getCurrentUsername();
            if (!username.equals("ANONYMOUS")) {
                String ipAddress = getClientIpAddress();
                securityEventLogger.logSuspiciousActivity(
                        "Attempted access to non-existent resource: " + ex.getMessage(),
                        username,
                        ipAddress
                );
            }
        } catch (Exception e) {
            // Ignorera fel i loggning för att undvika oändliga loopar
        }

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Resource Not Found",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Hanterar åtkomstfel från Spring Security.
     * 403 Forbidden när användaren är inloggad men saknar behörighet.
     * KRITISK SÄKERHETSHÄNDELSE - loggas alltid
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {

        // Logga som kritisk säkerhetshändelse
        try {
            String username = getCurrentUsername();
            String ipAddress = getClientIpAddress();
            String resource = request.getDescription(false).replace("uri=", "");
            securityEventLogger.logAccessDenied(username, resource, ipAddress);
        } catch (Exception e) {
            // Ignorera fel i loggning
        }

        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Access Denied",
                "Du har inte behörighet att komma åt denna resurs",
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Hanterar fel med roller.
     */
    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRoleException(
            InvalidRoleException ex, WebRequest request) {

        // Logga som säkerhetshändelse - någon försöker sätta ogiltig roll
        try {
            String username = getCurrentUsername();
            String ipAddress = getClientIpAddress();
            securityEventLogger.logSuspiciousActivity(
                    "Invalid role assignment attempt: " + ex.getMessage(),
                    username,
                    ipAddress
            );
        } catch (Exception e) {
            // Ignorera fel i loggning
        }

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Role",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Hanterar valideringsfel från @Valid annotationer.
     * Samlar ihop alla fält som failade och returnerar en lista.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {

        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }

        // Logga som potentiell säkerhetshändelse om många valideringsfel
        try {
            if (errors.size() > 5) {
                String username = getCurrentUsername();
                String ipAddress = getClientIpAddress();
                securityEventLogger.logSuspiciousActivity(
                        "Multiple validation errors (possible fuzzing): " + errors.size() + " errors",
                        username,
                        ipAddress
                );
            }
        } catch (Exception e) {
            // Ignorera fel i loggning
        }

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "Följande fält innehåller fel: " + String.join(", ", errors),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Hanterar constraint violations från Bean Validation.
     * För valideringsfel på parametrar och path variables.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {

        Map<String, String> validationErrors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            validationErrors.put(fieldName, errorMessage);
        }

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Constraint Violation",
                "Constraint validering misslyckades: " + validationErrors.toString(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Fallback för alla andra exceptions som vi inte har specifik hantering för.
     * Returnerar 500 Internal Server Error med generiskt meddelande.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {

        // Logga som systemfel - kan vara säkerhetsrelaterat
        try {
            String username = getCurrentUsername();
            String ipAddress = getClientIpAddress();
            securityEventLogger.logSecuritySystemError(
                    "Unhandled exception: " + ex.getClass().getSimpleName() + " - " + ex.getMessage(),
                    "GlobalExceptionHandler"
            );

            // Om det är RuntimeException eller liknande kan det vara attack
            if (ex instanceof RuntimeException) {
                securityEventLogger.logSuspiciousActivity(
                        "Runtime exception occurred: " + ex.getMessage(),
                        username,
                        ipAddress
                );
            }
        } catch (Exception e) {
            // Ignorera fel i loggning
        }

        // Logga det riktiga felet för utvecklare men visa inte känslig info till klienten
        System.err.println("Oväntat fel: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
        ex.printStackTrace();

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Ett oväntat fel uppstod. Kontakta support om problemet kvarstår.",
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /*
     * Hjälpmetod för att få tag på användarnamnet för den inloggade användaren
     * Returnerar "ANONYMOUS" för icke-inloggade användare
     */
    private String getCurrentUsername() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                return auth.getName();
            }
            return "ANONYMOUS";
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    /*
     * Hjälpmetod för att få tag på klientens IP-adress
     * Kollar flera headers för att hantera proxies och load balancers
     */
    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                // Kolla vanliga headers för proxy/load balancer setup
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty() && !xForwardedFor.equalsIgnoreCase("unknown")) {
                    // X-Forwarded-For kan innehålla flera IPs, ta den första
                    return xForwardedFor.split(",")[0].trim();
                }

                String xRealIp = request.getHeader("X-Real-IP");
                if (xRealIp != null && !xRealIp.isEmpty() && !xRealIp.equalsIgnoreCase("unknown")) {
                    return xRealIp;
                }

                // Fallback till standard remote address
                return request.getRemoteAddr();
            }
            return "UNKNOWN";
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }
}