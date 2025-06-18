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
import se.secure.springapp.securespringapp.dto.ErrorResponse;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Enhetstester för GlobalExceptionHandler.
 * Testar att olika exceptions hanteras korrekt och returnerar rätt HTTP-statuskoder.
 * FIXAD VERSION - Använder korrekt ErrorResponse-typ för metoderna.
 *
 * @author Gustav
 * @version 3.0 - Fixad för att matcha ErrorResponse-returtyper
 * @since 2025-06-16
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
        ResponseEntity<String> response = exceptionHandler.handleUserNotFoundException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Användare hittades inte", response.getBody());
    }

    @Test
    void handleAccessDeniedException_ShouldReturn403() {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("Åtkomst nekad");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDeniedException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("permission"));
        assertNotNull(response.getBody().getTimestamp());
        assertEquals("Access Denied", response.getBody().getError());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleAuthenticationException_ShouldReturn401() {
        // Arrange
        AuthenticationException exception = new AuthenticationException("Autentisering misslyckades") {};

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAuthenticationException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("Authentication Failed", response.getBody().getError());
        assertEquals("Invalid username or password", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleBadCredentialsException_ShouldReturn401() {
        // Arrange
        BadCredentialsException exception = new BadCredentialsException("Fel användaruppgifter");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadCredentialsException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("Invalid Credentials", response.getBody().getError());
        assertEquals("Username or password is incorrect", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleGenericException_ShouldReturn500() {
        // Arrange
        Exception exception = new RuntimeException("Oväntat fel");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("unexpected error"));
        assertNotNull(response.getBody().getTimestamp());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleIllegalArgumentException_ShouldReturn400() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Ogiltiga parametrar");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Ogiltiga parametrar", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void allErrorResponses_ShouldHaveTimestamp() {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("Åtkomst nekad");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDeniedException(exception, webRequest);

        // Assert
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void allErrorResponses_ShouldIncludePath() {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("Åtkomst nekad");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDeniedException(exception, webRequest);

        // Assert
        assertNotNull(response.getBody().getPath());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void errorResponse_ShouldHaveAllRequiredFields() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Test error");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(exception, webRequest);

        // Assert
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertNotNull(errorResponse.getTimestamp());
        assertEquals(400, errorResponse.getStatus());
        assertEquals("Bad Request", errorResponse.getError());
        assertEquals("Test error", errorResponse.getMessage());
        assertEquals("/api/test", errorResponse.getPath());
    }
}