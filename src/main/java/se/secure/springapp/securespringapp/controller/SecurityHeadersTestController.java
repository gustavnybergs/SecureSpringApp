package se.secure.springapp.securespringapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Test-controller för att verifiera att våra säkerhetsheaders fungerar korrekt.
 * Jag skapade denna för User Story #4 (35 enligt github commit) så vi enkelt kan kontrollera att
 * SecurityHeadersFilter lägger till rätt headers på alla HTTP-svar.
 *
 * Använd browser dev tools (F12 -> Network) för att se att headers som
 * X-Frame-Options, CSP och andra säkerhetsheaders faktiskt sätts.
 *
 * @author Utvecklare 3
 * @version 1.0
 * @since 2025-06-09
 */
@RestController
@RequestMapping("/api/public/security-test")
public class SecurityHeadersTestController {

    /**
     * Endpoint för att testa att säkerhetsheaders sätts korrekt.
     * Öppna detta i webbläsaren och kolla Network tab i dev tools
     * för att verifiera att vårt SecurityHeadersFilter fungerar som det ska.
     *
     * @return ResponseEntity med instruktioner för hur man kontrollerar headers
     */
    @GetMapping("/headers")
    public ResponseEntity<Map<String, String>> testHeaders() {
        Map<String, String> response = new HashMap<>();

        response.put("message", "Kolla Network tab i dev tools för att se säkerhetsheaders");
        response.put("expectedHeaders", "Content-Security-Policy, X-Frame-Options, X-Content-Type-Options, m.fl.");
        response.put("instructions", "Öppna F12 -> Network -> ladda om sidan -> klicka på denna request");

        return ResponseEntity.ok(response);
    }

    /**
     * Testar Content Security Policy genom att returnera HTML med scripts.
     * Om CSP fungerar ska externa scripts blockeras och fel visas i console.
     * Praktiskt för att demo att våra säkerhetsheaders faktiskt skyddar mot XSS.
     *
     * @return ResponseEntity med HTML som testar CSP-regler
     */
    @GetMapping("/csp-test")
    public ResponseEntity<String> testCSP() {
        String html = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>CSP Test</title>
            </head>
            <body>
                <h1>CSP Test</h1>
                <p>Om CSP fungerar ska externa scripts nedan blockeras:</p>
                
                <!-- Detta ska fungera (samma origin) -->
                <script>console.log('Lokalt script fungerar');</script>
                
                <!-- Detta ska blockeras av CSP -->
                <script src="https://evil-site.com/malicious.js"></script>
                
                <p>Kolla browser console för CSP-fel</p>
            </body>
            </html>
            """;

        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=utf-8")
                .body(html);
    }
}