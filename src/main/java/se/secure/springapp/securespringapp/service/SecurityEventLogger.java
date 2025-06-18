package se.secure.springapp.securespringapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Komponent för säkerhetsloggning enligt projektets VG-krav.
 * Loggar registrering och borttagning av användare samt andra säkerhetshändelser.
 *
 * @author Gustav
 * @version 3.0 - Förenklad utan IP-krav
 * @since 2025-06-09
 */
@Component
public class SecurityEventLogger {

    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Loggar registrering av ny användare (VG-KRAV).
     */
    public void logUserRegistration(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }

        securityLogger.info("Användarregistrering - Email: {} vid {}",
                email, LocalDateTime.now().format(formatter));
    }

    /**
     * Loggar borttagning av användare (VG-KRAV).
     */
    public void logUserDeletion(String userEmail, String deletedBy) {
        if (userEmail == null || deletedBy == null) {
            throw new IllegalArgumentException("UserEmail and deletedBy cannot be null");
        }

        securityLogger.info("Användarborttagning - Användare: {} raderad av: {} vid {}",
                userEmail, deletedBy, LocalDateTime.now().format(formatter));
    }

    /**
     * Loggar lyckad inloggning.
     */
    public void logSuccessfulAuthentication(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }

        securityLogger.info("Lyckad inloggning - Användare: {} vid {}",
                email, LocalDateTime.now().format(formatter));
    }

    /**
     * Loggar misslyckad inloggning.
     */
    public void logFailedAuthentication(String email, String reason) {
        if (reason == null) {
            throw new IllegalArgumentException("Reason cannot be null");
        }

        securityLogger.warn("Misslyckad inloggning - Email: {} - Orsak: {} vid {}",
                email != null ? email : "UNKNOWN", reason, LocalDateTime.now().format(formatter));
    }

    /**
     * Loggar admin-aktiviteter.
     */
    public void logAdminActivity(String adminEmail, String action, String targetUser) {
        if (adminEmail == null || action == null) {
            throw new IllegalArgumentException("AdminEmail and action cannot be null");
        }

        securityLogger.info("Admin-åtgärd - Action: {} av: {} på: {} vid {}",
                action, adminEmail, targetUser != null ? targetUser : "systemet",
                LocalDateTime.now().format(formatter));
    }

    /**
     * Loggar säkerhetsincidenter.
     */
    public void logSecurityIncident(String incidentType, String description, String userEmail) {
        if (incidentType == null || description == null) {
            throw new IllegalArgumentException("IncidentType and description cannot be null");
        }

        securityLogger.error("SÄKERHETSINCIDENT - {}: {} användare: {} vid {}",
                incidentType, description,
                userEmail != null ? userEmail : "ANONYMOUS", LocalDateTime.now().format(formatter));
    }
}