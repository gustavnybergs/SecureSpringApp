package se.secure.springapp.securespringapp.controller;

import se.secure.springapp.securespringapp.service.SecurityEventLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/*
 * Test-controller för att prova säkerhetsloggning
 * Simulerar olika säkerhetshändelser för att testa att loggning fungerar
 */
@RestController
@RequestMapping("/api/public/security-log-test")
public class SecurityLoggingTestController {

    private final SecurityEventLogger securityEventLogger;

    @Autowired
    public SecurityLoggingTestController(SecurityEventLogger securityEventLogger) {
        this.securityEventLogger = securityEventLogger;
    }

    /*
     * Testar loggning av lyckad inloggning
     */
    @PostMapping("/test-login")
    public ResponseEntity<Map<String, String>> testLoginLog(
            @RequestParam String username,
            HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();
        securityEventLogger.logSuccessfulLogin(username, ipAddress);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Lyckad inloggning loggad för " + username);
        response.put("check", "Kolla loggarna för säkerhetshändelsen");

        return ResponseEntity.ok(response);
    }

    /*
     * Testar loggning av misslyckad inloggning
     */
    @PostMapping("/test-failed-login")
    public ResponseEntity<Map<String, String>> testFailedLoginLog(
            @RequestParam String username,
            HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();
        securityEventLogger.logFailedLogin(username, ipAddress, "Fel lösenord");

        Map<String, String> response = new HashMap<>();
        response.put("message", "Misslyckad inloggning loggad för " + username);
        response.put("check", "Kolla loggarna för säkerhetshändelsen");

        return ResponseEntity.ok(response);
    }

    /*
     * Testar loggning av misstänkt aktivitet
     */
    @PostMapping("/test-suspicious")
    public ResponseEntity<Map<String, String>> testSuspiciousLog(
            @RequestParam String description,
            HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();
        securityEventLogger.logSuspiciousActivity(description, "test-user", ipAddress);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Misstänkt aktivitet loggad: " + description);
        response.put("check", "Kolla loggarna för säkerhetshändelsen");

        return ResponseEntity.ok(response);
    }

    /*
     * Endpoint som kräver admin-roll för att testa åtkomstloggning
     * Kommer loggas via AOP när den anropas
     */
    @GetMapping("/admin-only")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, String>> adminOnlyEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Detta endpoint kräver admin-behörighet");
        response.put("info", "Åtkomst till detta endpoint loggas automatiskt");

        return ResponseEntity.ok(response);
    }
}