package se.secure.springapp.securespringapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
 * Service för att logga säkerhetshändelser i applikationen
 * Centraliserad plats för all säkerhetsrelaterad loggning
 * Loggar inloggningar, misslyckade försök, behörighetsfel etc
 */
@Service
public class SecurityEventLogger {

    private static final Logger logger = LoggerFactory.getLogger(SecurityEventLogger.class);
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /*
     * Loggar lyckade inloggningar
     * Sparar användarnamn och tidpunkt för granskning
     */
    public void logSuccessfulLogin(String username, String ipAddress) {
        String timestamp = LocalDateTime.now().format(formatter);
        String message = String.format("SUCCESSFUL_LOGIN - User: %s, IP: %s, Time: %s",
                username, ipAddress, timestamp);

        securityLogger.info(message);
        logger.info("Användare {} loggade in framgångsrikt från IP {}", username, ipAddress);
    }

    /*
     * Loggar misslyckade inloggningsförsök
     * Viktigt för att upptäcka brute force-attacker
     */
    public void logFailedLogin(String username, String ipAddress, String reason) {
        String timestamp = LocalDateTime.now().format(formatter);
        String message = String.format("FAILED_LOGIN - User: %s, IP: %s, Reason: %s, Time: %s",
                username, ipAddress, reason, timestamp);

        securityLogger.warn(message);
        logger.warn("Misslyckad inloggning för användare {} från IP {}: {}", username, ipAddress, reason);
    }

    /*
     * Loggar när användare försöker komma åt resurser de inte har tillgång till
     * Hjälper att upptäcka privilege escalation-försök
     */
    public void logAccessDenied(String username, String resource, String ipAddress) {
        String timestamp = LocalDateTime.now().format(formatter);
        String message = String.format("ACCESS_DENIED - User: %s, Resource: %s, IP: %s, Time: %s",
                username, resource, ipAddress, timestamp);

        securityLogger.warn(message);
        logger.warn("Åtkomst nekad för användare {} till resurs {} från IP {}", username, resource, ipAddress);
    }

    /*
     * Loggar när nya användare registreras
     * Bra att ha koll på när nya konton skapas
     */
    public void logUserRegistration(String username, String ipAddress) {
        String timestamp = LocalDateTime.now().format(formatter);
        String message = String.format("USER_REGISTRATION - User: %s, IP: %s, Time: %s",
                username, ipAddress, timestamp);

        securityLogger.info(message);
        logger.info("Ny användare registrerad: {} från IP {}", username, ipAddress);
    }

    /*
     * Loggar när administratörer ändrar användarroller
     * Kritisk säkerhetshändelse som alltid ska loggas
     */
    public void logRoleChange(String adminUsername, String targetUsername, String oldRole, String newRole, String ipAddress) {
        String timestamp = LocalDateTime.now().format(formatter);
        String message = String.format("ROLE_CHANGE - Admin: %s, Target: %s, Old: %s, New: %s, IP: %s, Time: %s",
                adminUsername, targetUsername, oldRole, newRole, ipAddress, timestamp);

        securityLogger.warn(message);
        logger.warn("Admin {} ändrade roll för {} från {} till {} (IP: {})",
                adminUsername, targetUsername, oldRole, newRole, ipAddress);
    }

    /*
     * Loggar när användare tas bort från systemet
     * Också en kritisk händelse som ska spåras
     */
    public void logUserDeletion(String adminUsername, String deletedUsername, String ipAddress) {
        String timestamp = LocalDateTime.now().format(formatter);
        String message = String.format("USER_DELETION - Admin: %s, Deleted: %s, IP: %s, Time: %s",
                adminUsername, deletedUsername, ipAddress, timestamp);

        securityLogger.warn(message);
        logger.warn("Admin {} tog bort användare {} (IP: {})", adminUsername, deletedUsername, ipAddress);
    }

    /*
     * Loggar misstänkta säkerhetshändelser
     * För saker som JWT-manipulering, SQL injection-försök etc
     */
    public void logSuspiciousActivity(String description, String username, String ipAddress) {
        String timestamp = LocalDateTime.now().format(formatter);
        String message = String.format("SUSPICIOUS_ACTIVITY - Description: %s, User: %s, IP: %s, Time: %s",
                description, username, ipAddress, timestamp);

        securityLogger.error(message);
        logger.error("Misstänkt aktivitet: {} (Användare: {}, IP: {})", description, username, ipAddress);
    }

    /*
     * Loggar systemfel som kan vara säkerhetsrelaterade
     * Tex databasfel, konfigurationsfel etc
     */
    public void logSecuritySystemError(String errorDescription, String component) {
        String timestamp = LocalDateTime.now().format(formatter);
        String message = String.format("SECURITY_SYSTEM_ERROR - Component: %s, Error: %s, Time: %s",
                component, errorDescription, timestamp);

        securityLogger.error(message);
        logger.error("Säkerhetssystemfel i {}: {}", component, errorDescription);
    }
}