package se.secure.springapp.securespringapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Komponent för säkerhetsloggning enligt projektets krav på User Story #7.
 * Vi behövde en central plats för att logga alla säkerhetshändelser så jag
 * skapade denna klass för att hantera det. Använder MDC för att strukturera
 * loggdata vilket gör det lättare att analysera loggar senare.
 *
 * Jag valde att logga olika typer av händelser som autentisering, resursåtkomst
 * och adminaktiviteter eftersom det var det som krävdes för projektet.
 * Alla metoder kastar IllegalArgumentException om obligatoriska parametrar saknas.
 *
 * @author Utvecklare 3
 * @version 1.0
 * @since 2025-06-09
 */
@Component
public class SecurityEventLogger {

    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Loggar när användare lyckas logga in.
     * Jag tänkte att det är viktigt att spåra alla lyckade inloggningar
     * för att kunna se normala användningsmönster i systemet.
     *
     * @param email vilken användare som loggade in
     * @param ipAddress från vilken IP-adress
     * @throws IllegalArgumentException om email eller ipAddress är null
     */
    public void logSuccessfulAuthentication(String email, String ipAddress) {
        if (email == null || ipAddress == null) {
            throw new IllegalArgumentException("Email and IP address cannot be null");
        }

        try {
            MDC.put("eventType", "AUTH_SUCCESS");
            MDC.put("userEmail", email);
            MDC.put("ipAddress", ipAddress);
            MDC.put("timestamp", LocalDateTime.now().format(formatter));

            securityLogger.info("Lyckad inloggning för användare: {}", email);
        } finally {
            MDC.clear();
        }
    }

    /**
     * Loggar misslyckade inloggningsförsök.
     * Detta är superviktigt för säkerheten - vi måste kunna se om någon
     * försöker brute-force attack eller liknande mot vårt system.
     *
     * @param email vilken email som användes (kan vara null om ogiltigt)
     * @param ipAddress från vilken IP
     * @param reason varför det misslyckades
     * @throws IllegalArgumentException om ipAddress eller reason är null
     */
    public void logFailedAuthentication(String email, String ipAddress, String reason) {
        if (ipAddress == null || reason == null) {
            throw new IllegalArgumentException("IP address and reason cannot be null");
        }

        try {
            MDC.put("eventType", "AUTH_FAILURE");
            MDC.put("userEmail", email != null ? email : "UNKNOWN");
            MDC.put("ipAddress", ipAddress);
            MDC.put("failureReason", reason);
            MDC.put("timestamp", LocalDateTime.now().format(formatter));

            securityLogger.warn("Misslyckad inloggning för email: {} - Orsak: {}",
                    email != null ? email : "UNKNOWN", reason);
        } finally {
            MDC.clear();
        }
    }

    /**
     * Loggar när användare kommer åt skyddade resurser.
     * Behövs för att spåra vem som gör vad i systemet, särskilt viktigt
     * för audit trails och säkerhetsgranskning.
     *
     * @param userEmail vilken användare
     * @param resource vilken resurs/endpoint
     * @param method vilken HTTP-metod (GET, POST etc.)
     * @param ipAddress från vilken IP
     * @throws IllegalArgumentException om någon parameter är null
     */
    public void logResourceAccess(String userEmail, String resource, String method, String ipAddress) {
        if (userEmail == null || resource == null || method == null || ipAddress == null) {
            throw new IllegalArgumentException("All parameters must be non-null");
        }

        try {
            MDC.put("eventType", "RESOURCE_ACCESS");
            MDC.put("userEmail", userEmail);
            MDC.put("resource", resource);
            MDC.put("method", method);
            MDC.put("ipAddress", ipAddress);
            MDC.put("timestamp", LocalDateTime.now().format(formatter));

            securityLogger.info("Resursåtkomst: {} {} av användare: {}", method, resource, userEmail);
        } finally {
            MDC.clear();
        }
    }

    /**
     * Loggar vad administratörer gör i systemet.
     * Eftersom admins har höga behörigheter måste vi logga allt de gör.
     * Det här är kritiskt för säkerheten och för att följa regler om loggning.
     *
     * @param adminEmail vilken admin som agerar
     * @param action vad de gör
     * @param targetUser vilken användare som påverkas (kan vara null)
     * @param ipAddress från vilken IP
     * @throws IllegalArgumentException om adminEmail, action eller ipAddress är null
     */
    public void logAdminActivity(String adminEmail, String action, String targetUser, String ipAddress) {
        if (adminEmail == null || action == null || ipAddress == null) {
            throw new IllegalArgumentException("AdminEmail, action and IP address cannot be null");
        }

        try {
            MDC.put("eventType", "ADMIN_ACTION");
            MDC.put("adminEmail", adminEmail);
            MDC.put("action", action);
            MDC.put("targetUser", targetUser != null ? targetUser : "N/A");
            MDC.put("ipAddress", ipAddress);
            MDC.put("timestamp", LocalDateTime.now().format(formatter));

            securityLogger.info("Admin-åtgärd: {} utförd av: {} på: {}",
                    action, adminEmail, targetUser != null ? targetUser : "systemet");
        } finally {
            MDC.clear();
        }
    }

    /**
     * Loggar säkerhetsincidenter och misstänkta aktiviteter.
     * Om vi upptäcker något konstigt eller misstänkt måste vi logga det
     * så vi kan analysera vad som hände senare.
     *
     * @param incidentType typ av incident (t.ex. "BRUTE_FORCE")
     * @param description vad som hände
     * @param ipAddress från vilken IP
     * @param userEmail vilken användare (kan vara null för anonyma attacker)
     * @throws IllegalArgumentException om incidentType, description eller ipAddress är null
     */
    public void logSecurityIncident(String incidentType, String description, String ipAddress, String userEmail) {
        if (incidentType == null || description == null || ipAddress == null) {
            throw new IllegalArgumentException("IncidentType, description and IP address cannot be null");
        }

        try {
            MDC.put("eventType", "SECURITY_INCIDENT");
            MDC.put("incidentType", incidentType);
            MDC.put("description", description);
            MDC.put("ipAddress", ipAddress);
            MDC.put("userEmail", userEmail != null ? userEmail : "ANONYMOUS");
            MDC.put("timestamp", LocalDateTime.now().format(formatter));

            securityLogger.error("SÄKERHETSINCIDENT - {}: {} från IP: {}",
                    incidentType, description, ipAddress);
        } finally {
            MDC.clear();
        }
    }

    /**
     * Loggar systemfel som kan påverka säkerheten.
     * Ibland händer det tekniska saker som kan vara säkerhetsproblem,
     * så jag gjorde en metod för att logga sånt också.
     *
     * @param eventType typ av systemhändelse
     * @param message huvudmeddelande
     * @param details extra detaljer (kan vara null)
     * @throws IllegalArgumentException om eventType eller message är null
     */
    public void logSystemSecurityEvent(String eventType, String message, String details) {
        if (eventType == null || message == null) {
            throw new IllegalArgumentException("EventType and message cannot be null");
        }

        try {
            MDC.put("eventType", "SYSTEM_SECURITY");
            MDC.put("systemEventType", eventType);
            MDC.put("message", message);
            MDC.put("details", details != null ? details : "");
            MDC.put("timestamp", LocalDateTime.now().format(formatter));

            securityLogger.warn("System säkerhetshändelse - {}: {}", eventType, message);
        } finally {
            MDC.clear();
        }
    }

    /**
     * Loggar när säkerhetsinställningar ändras.
     * Vi måste hålla koll på alla ändringar av säkerhetskonfiguration
     * eftersom det kan påverka hela systemets säkerhet.
     *
     * @param configType vilken typ av konfiguration
     * @param change vad som ändrades
     * @param adminEmail vem som ändrade
     * @throws IllegalArgumentException om någon parameter är null
     */
    public void logSecurityConfigChange(String configType, String change, String adminEmail) {
        if (configType == null || change == null || adminEmail == null) {
            throw new IllegalArgumentException("All parameters must be non-null");
        }

        try {
            MDC.put("eventType", "CONFIG_CHANGE");
            MDC.put("configType", configType);
            MDC.put("change", change);
            MDC.put("adminEmail", adminEmail);
            MDC.put("timestamp", LocalDateTime.now().format(formatter));

            securityLogger.info("Säkerhetskonfiguration ändrad - {}: {} av: {}",
                    configType, change, adminEmail);
        } finally {
            MDC.clear();
        }
    }
}