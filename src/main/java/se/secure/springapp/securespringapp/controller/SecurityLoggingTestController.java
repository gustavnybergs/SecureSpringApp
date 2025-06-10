package se.secure.springapp.securespringapp.controller;

import se.secure.springapp.securespringapp.service.SecurityEventLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Test-controller för att prova och demo våra säkerhetsloggningsfunktioner.
 * Jag skapade denna för User Story #7 (37 enligt github commit) så vi kan simulera olika säkerhetshändelser
 * och verifiera att SecurityEventLogger fungerar som den ska.
 *
 * Använd dessa endpoints för att generera testdata i loggarna och se
 * att säkerhetsövervakning fungerar korrekt innan vi går live.
 *
 * TODO: Väntar på att Utvecklare 2 implementerar SecurityEventLogger
 *
 * @author Utvecklare 3
 * @version 1.0
 * @since 2025-06-09
 */
@RestController
@RequestMapping("/api/public/security-log-test")
public class SecurityLoggingTestController {

    private final SecurityEventLogger securityEventLogger;

    @Autowired
    public SecurityLoggingTestController(SecurityEventLogger securityEventLogger) {
        this.securityEventLogger = securityEventLogger;
    }

    /**
     * Simulerar en lyckad inloggning för att testa säkerhetsloggning.
     * Kör detta och kolla sedan loggfilerna för att se att händelsen
     * loggades korrekt med rätt format och alla nödvändiga detaljer.
     *
     * TODO: Implementeras av Utvecklare 2 - SecurityEventLogger saknas
     *
     * @param username användarnamn som ska loggas som inloggad
     * @param request HTTP-request för att hämta IP-adress
     * @return ResponseEntity med bekräftelse att loggning utfördes
     */
    @PostMapping("/test-login")
    public ResponseEntity<Map<String, String>> testLoginLog(
            @RequestParam String username,
            HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();
        // TODO: Aktivera när Utvecklare 2 implementerat SecurityEventLogger
        // securityEventLogger.logSuccessfulLogin(username, ipAddress);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Test endpoint för lyckad inloggning - väntar på SecurityEventLogger");
        response.put("todo", "Utvecklare 2 ska implementera SecurityEventLogger");
        response.put("username", username);
        response.put("ipAddress", ipAddress);

        return ResponseEntity.ok(response);
    }

    /**
     * Simulerar misslyckad inloggning för att testa säkerhetsloggning.
     * Viktigt för att testa att vi kan upptäcka och logga potentiella
     * säkerhetsattacker som brute-force eller felaktiga inloggningsförsök.
     *
     * TODO: Implementeras av Utvecklare 2 - SecurityEventLogger saknas
     *
     * @param username användarnamn för den misslyckade inloggningen
     * @param request HTTP-request för IP-adress och kontext
     * @return ResponseEntity med bekräftelse att säkerhetshändelsen loggades
     */
    @PostMapping("/test-failed-login")
    public ResponseEntity<Map<String, String>> testFailedLoginLog(
            @RequestParam String username,
            HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();
        // TODO: Aktivera när Utvecklare 2 implementerat SecurityEventLogger
        // securityEventLogger.logFailedLogin(username, ipAddress, "Fel lösenord");

        Map<String, String> response = new HashMap<>();
        response.put("message", "Test endpoint för misslyckad inloggning - väntar på SecurityEventLogger");
        response.put("todo", "Utvecklare 2 ska implementera SecurityEventLogger");
        response.put("username", username);
        response.put("ipAddress", ipAddress);

        return ResponseEntity.ok(response);
    }

    /**
     * Simulerar misstänkt säkerhetsaktivitet för loggning.
     * Använd detta för att testa att vi kan logga och spåra konstiga
     * aktiviteter som kan indikera säkerhetsincidenter eller attacker.
     *
     * TODO: Implementeras av Utvecklare 2 - SecurityEventLogger saknas
     *
     * @param description beskrivning av den misstänkta aktiviteten
     * @param request HTTP-request för säkerhetskontext
     * @return ResponseEntity med bekräftelse att incidenten loggades
     */
    @PostMapping("/test-suspicious")
    public ResponseEntity<Map<String, String>> testSuspiciousLog(
            @RequestParam String description,
            HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();
        // TODO: Aktivera när Utvecklare 2 implementerat SecurityEventLogger
        // securityEventLogger.logSuspiciousActivity(description, "test-user", ipAddress);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Test endpoint för misstänkt aktivitet - väntar på SecurityEventLogger");
        response.put("todo", "Utvecklare 2 ska implementera SecurityEventLogger");
        response.put("description", description);
        response.put("ipAddress", ipAddress);

        return ResponseEntity.ok(response);
    }

    /**
     * Skyddat endpoint som bara admins får komma åt.
     * Detta testar både åtkomstkontroll och säkerhetsloggning samtidigt.
     * När admin-användare kommer åt detta ska det loggas automatiskt.
     *
     * @return ResponseEntity med bekräftelse för admin-användare
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