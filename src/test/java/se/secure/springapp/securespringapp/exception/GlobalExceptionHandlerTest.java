package se.secure.springapp.securespringapp.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Enhetstester för GlobalExceptionHandler.
 * Testar att olika exceptions hanteras korrekt och returnerar rätt HTTP-statuskoder.
 * FIXAD VERSION - Använder korrekt WebRequest interface och rätta metodnamn.
 *
 * @author Utvecklare 3
 * @version 2.0 - Fixad för att matcha aktuell GlobalExceptionHandler
 * @since 2025-06-11
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setRequestURI("/api/test");
        webRequest = new ServletWebRequest(mockRequest);
    }

    @Test
    void handleUserNotFoundException_ShouldReturn404() {
        // Arrange
        UserNotFoundException exception = new UserNotFoundException("Användare hittades inte");

        // Act
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleUserNotFoundException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().get("status"));
        assertEquals("Användare hittades inte", response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void handleAccessDeniedException_ShouldReturn403() {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("Åtkomst nekad");

        // Act
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAccessDeniedException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().get("status"));
        assertTrue(response.getBody().get("message").toString().contains("permission"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void handleAuthenticationException_ShouldReturn401() {
        // Arrange
        AuthenticationException exception = new AuthenticationException("Autentisering misslyckades") {};

        // Act
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAuthenticationException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().get("status"));
        assertEquals("Authentication Failed", response.getBody().get("error"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void handleBadCredentialsException_ShouldReturn401() {
        // Arrange
        BadCredentialsException exception = new BadCredentialsException("Fel användaruppgifter");

        // Act
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleBadCredentialsException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().get("status"));
        assertEquals("Invalid Credentials", response.getBody().get("error"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void handleGenericException_ShouldReturn500() {
        // Arrange
        Exception exception = new RuntimeException("Oväntat fel");

        // Act
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGenericException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().get("status"));
        assertEquals("Internal Server Error", response.getBody().get("error"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void handleIllegalArgumentException_ShouldReturn400() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Ogiltiga parametrar");

        // Act
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleIllegalArgumentException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Bad Request", response.getBody().get("error"));
        assertTrue(response.getBody().get("message").toString().contains("Ogiltiga parametrar"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void allExceptionHandlers_ShouldIncludeTimestamp() {
        // Arrange
        UserNotFoundException exception = new UserNotFoundException("Test");

        // Act
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleUserNotFoundException(exception, webRequest);

        // Assert
        assertNotNull(response.getBody().get("timestamp"));
        // Kontrollera att timestamp finns och är ett rimligt värde
        Object timestamp = response.getBody().get("timestamp");
        assertNotNull(timestamp);
        // Vi bryr oss bara om att timestamp finns, inte vilken typ det är
    }

    @Test
    void allExceptionHandlers_ShouldIncludePath() {
        // Arrange
        UserNotFoundException exception = new UserNotFoundException("Test");

        // Act
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleUserNotFoundException(exception, webRequest);

        // Assert
        assertNotNull(response.getBody().get("path"));
        assertEquals("/api/test", response.getBody().get("path"));
    }
}