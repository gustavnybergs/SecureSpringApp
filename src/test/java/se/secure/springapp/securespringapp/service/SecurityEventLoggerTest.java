package se.secure.springapp.securespringapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Enhetstester för SecurityEventLogger.
 * Testar att säkerhetsloggning fungerar korrekt och validerar parametrar.
 *
 * @author Gustav
 * @version 2.0 - Uppdaterad utan IP-krav
 * @since 2025-06-10
 */
@ExtendWith(MockitoExtension.class)
class SecurityEventLoggerTest {

    private SecurityEventLogger securityEventLogger;

    @BeforeEach
    void setUp() {
        securityEventLogger = new SecurityEventLogger();
    }

    @Test
    void logUserRegistration_ShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            securityEventLogger.logUserRegistration("test@example.com");
        });
    }

    @Test
    void logUserRegistration_ShouldThrowExceptionForNullEmail() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            securityEventLogger.logUserRegistration(null);
        });

        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    void logUserDeletion_ShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            securityEventLogger.logUserDeletion("user@example.com", "admin@example.com");
        });
    }

    @Test
    void logUserDeletion_ShouldThrowExceptionForNullUserEmail() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            securityEventLogger.logUserDeletion(null, "admin@example.com");
        });

        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    void logUserDeletion_ShouldThrowExceptionForNullDeletedBy() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            securityEventLogger.logUserDeletion("user@example.com", null);
        });

        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    void logSuccessfulAuthentication_ShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            securityEventLogger.logSuccessfulAuthentication("test@example.com");
        });
    }

    @Test
    void logSuccessfulAuthentication_ShouldThrowExceptionForNullEmail() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            securityEventLogger.logSuccessfulAuthentication(null);
        });

        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    void logFailedAuthentication_ShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            securityEventLogger.logFailedAuthentication("test@example.com", "Fel lösenord");
        });
    }

    @Test
    void logFailedAuthentication_ShouldAllowNullEmail() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            securityEventLogger.logFailedAuthentication(null, "Ogiltig email");
        });
    }

    @Test
    void logFailedAuthentication_ShouldThrowExceptionForNullReason() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            securityEventLogger.logFailedAuthentication("test@example.com", null);
        });

        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    void logAdminActivity_ShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            securityEventLogger.logAdminActivity("admin@example.com", "USER_DELETE", "target@example.com");
        });
    }

    @Test
    void logAdminActivity_ShouldAllowNullTargetUser() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            securityEventLogger.logAdminActivity("admin@example.com", "SYSTEM_CONFIG", null);
        });
    }

    @Test
    void logAdminActivity_ShouldThrowExceptionForNullAdminEmail() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            securityEventLogger.logAdminActivity(null, "USER_DELETE", "target@example.com");
        });

        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    void logAdminActivity_ShouldThrowExceptionForNullAction() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            securityEventLogger.logAdminActivity("admin@example.com", null, "target@example.com");
        });

        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    void logSecurityIncident_ShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            securityEventLogger.logSecurityIncident("BRUTE_FORCE", "Flera misslyckade inloggningar", "attacker@evil.com");
        });
    }

    @Test
    void logSecurityIncident_ShouldAllowNullUserEmail() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            securityEventLogger.logSecurityIncident("SUSPICIOUS_REQUEST", "Onormal request pattern", null);
        });
    }

    @Test
    void logSecurityIncident_ShouldThrowExceptionForNullIncidentType() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            securityEventLogger.logSecurityIncident(null, "Test incident", "test@example.com");
        });

        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    void logSecurityIncident_ShouldThrowExceptionForNullDescription() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            securityEventLogger.logSecurityIncident("BRUTE_FORCE", null, "test@example.com");
        });

        assertTrue(exception.getMessage().contains("cannot be null"));
    }
}