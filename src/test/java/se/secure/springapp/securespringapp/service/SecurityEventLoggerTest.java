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
 * @version 1.0
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
    void logSuccessfulAuthentication_ShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            securityEventLogger.logSuccessfulAuthentication("test@example.com", "192.168.1.1");
        });
    }

    @Test
    void logSuccessfulAuthentication_ShouldThrowExceptionForNullEmail() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            securityEventLogger.logSuccessfulAuthentication(null, "192.168.1.1");
        });

        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    void logSuccessfulAuthentication_ShouldThrowExceptionForNullIpAddress() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            securityEventLogger.logSuccessfulAuthentication("test@example.com", null);
        });

        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    void logFailedAuthentication_ShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            securityEventLogger.logFailedAuthentication("test@example.com", "192.168.1.1", "Fel lösenord");
        });
    }

    @Test
    void logFailedAuthentication_ShouldAllowNullEmail() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            securityEventLogger.logFailedAuthentication(null, "192.168.1.1", "Ogiltig email");
        });
    }

    @Test
    void logFailedAuthentication_ShouldThrowExceptionForNullIpAddress() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            securityEventLogger.logFailedAuthentication("test@example.com", null, "Fel lösenord");
        });

        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    void logFailedAuthentication_ShouldThrowExceptionForNullReason() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            securityEventLogger.logFailedAuthentication("test@example.com", "192.168.1.1", null);
        });

        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    void logResourceAccess_ShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            securityEventLogger.logResourceAccess("test@example.com", "/api/user/profile", "GET", "192.168.1.1");
        });
    }

    @Test
    void logResourceAccess_ShouldThrowExceptionForNullUserEmail() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            securityEventLogger.logResourceAccess(null, "/api/user/profile", "GET", "192.168.1.1");
        });

        assertTrue(exception.getMessage().contains("non-null"));
    }

    @Test
    void logAdminActivity_ShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            securityEventLogger.logAdminActivity("admin@example.com", "USER_DELETE", "target@example.com", "192.168.1.1");
        });
    }

    @Test
    void logAdminActivity_ShouldAllowNullTargetUser() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            securityEventLogger.logAdminActivity("admin@example.com", "SYSTEM_CONFIG", null, "192.168.1.1");
        });
    }

    @Test
    void logAdminActivity_ShouldThrowExceptionForNullAdminEmail() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            securityEventLogger.logAdminActivity(null, "USER_DELETE", "target@example.com", "192.168.1.1");
        });

        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    void logSecurityIncident_ShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            securityEventLogger.logSecurityIncident("BRUTE_FORCE", "Flera misslyckade inloggningar", "192.168.1.1", "attacker@evil.com");
        });
    }

    @Test
    void logSecurityIncident_ShouldAllowNullUserEmail() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            securityEventLogger.logSecurityIncident("SUSPICIOUS_REQUEST", "Onormal request pattern", "192.168.1.1", null);
        });
    }

    @Test
    void logSecurityIncident_ShouldThrowExceptionForNullIncidentType() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            securityEventLogger.logSecurityIncident(null, "Test incident", "192.168.1.1", "test@example.com");
        });

        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    void logSystemSecurityEvent_ShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            securityEventLogger.logSystemSecurityEvent("CONFIG_CHANGE", "Säkerhetsinställningar uppdaterade", "Admin panel");
        });
    }

    @Test
    void logSystemSecurityEvent_ShouldAllowNullDetails() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            securityEventLogger.logSystemSecurityEvent("STARTUP", "System startat", null);
        });
    }

    @Test
    void logSystemSecurityEvent_ShouldThrowExceptionForNullEventType() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            securityEventLogger.logSystemSecurityEvent(null, "Test meddelande", "Test detaljer");
        });

        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    void logSecurityConfigChange_ShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            securityEventLogger.logSecurityConfigChange("PASSWORD_POLICY", "Minsta längd ändrad till 10", "admin@example.com");
        });
    }

    @Test
    void logSecurityConfigChange_ShouldThrowExceptionForNullConfigType() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            securityEventLogger.logSecurityConfigChange(null, "Ändring gjord", "admin@example.com");
        });

        assertTrue(exception.getMessage().contains("non-null"));
    }
}