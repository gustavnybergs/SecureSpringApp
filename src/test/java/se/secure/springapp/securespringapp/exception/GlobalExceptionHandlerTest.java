package se.secure.springapp.securespringapp.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import se.secure.springapp.securespringapp.dto.ErrorResponse;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Enhetstester för GlobalExceptionHandler.
 * Testar att olika exceptions hanteras korrekt och returnerar rätt HTTP-statuskoder.
 *
 * @author Utvecklare 3
 * @version 1.0
 * @since 2025-06-10
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
    }

    @Test
    void handleUserNotFoundException_ShouldReturn404() {
        // Arrange
        UserNotFoundException exception = new UserNotFoundException("Användare hittades inte");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserNotFoundException(exception, request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Användare hittades inte", response.getBody().getMessage());
    }

    @Test
    void handleAccessDeniedException_ShouldReturn403() {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("Åtkomst nekad");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDeniedException(exception, request);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("behörighet"));
    }

    @Test
    void handleAuthenticationException_ShouldReturn401() {
        // Arrange
        AuthenticationException exception = new AuthenticationException("Autentisering misslyckades") {};

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAuthenticationException(exception, request);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("Autentisering"));
    }

    @Test
    void handleValidationException_ShouldReturn400WithFieldErrors() {
        // Arrange
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "test");
        bindingResult.addError(new FieldError("test", "email", "Email är obligatorisk"));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("Valideringsfel"));
        assertTrue(response.getBody().getMessage().contains("email"));
    }

    @Test
    void handleRuntimeException_ShouldReturn500() {
        // Arrange
        RuntimeException exception = new RuntimeException("Oväntat fel");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleRuntimeException(exception, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("internt fel"));
    }

    @Test
    void handleIllegalArgumentException_ShouldReturn400() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Ogiltiga parametrar");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(exception, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("Ogiltiga parametrar"));
    }

    @Test
    void allExceptions_ShouldHaveTimestamp() {
        // Arrange
        UserNotFoundException exception = new UserNotFoundException("Test");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserNotFoundException(exception, request);

        // Assert
        assertNotNull(response.getBody().getTimestamp());
    }
}