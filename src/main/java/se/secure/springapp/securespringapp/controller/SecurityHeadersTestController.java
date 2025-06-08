package se.secure.springapp.securespringapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/*
 * Controller för att testa att säkerhetsheaders sätts korrekt
 * Returnerar info om vilka headers som borde finnas
 * Använd browser dev tools för att verifiera att headers finns
 */
@RestController
@RequestMapping("/api/public/security-test")
public class SecurityHeadersTestController {

    /*
     * Endpoint för att testa säkerhetsheaders
     * Öppna denna i browser och kolla Network tab för att se headers
     */
    @GetMapping("/headers")
    public ResponseEntity<Map<String, String>> testHeaders() {
        Map<String, String> response = new HashMap<>();

        response.put("message", "Kolla Network tab i dev tools för att se säkerhetsheaders");
        response.put("expectedHeaders", "Content-Security-Policy, X-Frame-Options, X-Content-Type-Options, m.fl.");
        response.put("instructions", "Öppna F12 -> Network -> ladda om sidan -> klicka på denna request");

        return ResponseEntity.ok(response);
    }

    /*
     * Test för CSP (Content Security Policy)
     * Denna endpoint returnerar HTML som försöker ladda externa scripts
     * Om CSP fungerar ska externa scripts blockeras
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