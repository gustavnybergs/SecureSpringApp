package se.secure.springapp.securespringapp.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller för API-dokumentation och grundläggande systemkontroller.
 * Jag skapade denna för User Story #5 (39 enligt github commit) eftersom vi behövde enkla endpoints
 * som visar att API:et fungerar och ger information om dokumentationen.
 *
 * Dessa endpoints är publika så utvecklare kan snabbt kolla systemstatus
 * och hitta dokumentationen utan att behöva autentisera sig först.
 *
 * @author Gustav
 * @version 1.0
 * @since 2025-06-09
 */
@RestController
@RequestMapping("/api/public")
@Tag(name = "API Information", description = "Endpoints för API-status och dokumentation")
public class ApiDocumentationController {

    /**
     * Enkel hälsokontroll som visar att API:et är igång och fungerar.
     * Jag använder detta när jag utvecklar för att snabbt se att servern startade rätt.
     * Returnerar grundläggande systeminfo utan känsliga detaljer.
     *
     * @return ResponseEntity med systemstatus och grundläggande information
     */
    @Operation(
            summary = "API hälsokontroll",
            description = "Kontrollerar att API:et är igång och fungerar korrekt."
    )
    @ApiResponse(responseCode = "200", description = "API:et fungerar korrekt")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = Map.of(
                "status", "UP",
                "message", "SecureSpringApp API är igång",
                "timestamp", System.currentTimeMillis(),
                "version", "1.0.0"
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Returnerar översikt över API:et och var man hittar dokumentationen.
     * Praktiskt för nya utvecklare som ska förstå hur vårt API är uppbyggt
     * och vilka endpoints som finns tillgängliga.
     *
     * @return ResponseEntity med API-metadata och länkstrukturen
     */
    @Operation(
            summary = "API-information",
            description = "Returnerar information om API:et inklusive tillgängliga endpoints och dokumentation."
    )
    @ApiResponse(responseCode = "200", description = "API-information returnerad")
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> apiInfo() {
        Map<String, Object> response = Map.of(
                "name", "SecureSpringApp API",
                "version", "1.0.0",
                "description", "Säker REST API byggd med Spring Boot",
                "documentation", Map.of(
                        "swagger-ui", "/swagger-ui.html",
                        "openapi-docs", "/v3/api-docs",
                        "openapi-yaml", "/v3/api-docs.yaml"
                ),
                "endpoints", Map.of(
                        "authentication", "/api/auth/*",
                        "user-resources", "/api/user/*",
                        "admin-functions", "/api/admin/*",
                        "public", "/api/public/*"
                )
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Dolt endpoint som omdirigerar till Swagger UI.
     * Jag hade detta som backup ifall någon skulle leta efter dokumentation
     * på fel ställe. @Hidden gör att det inte syns i Swagger-dokumentationen.
     *
     * @return ResponseEntity med omdirigeringsinformation till Swagger UI
     */
    @Hidden  // Dölj detta endpoint från Swagger UI
    @GetMapping("/swagger-redirect")
    public ResponseEntity<Map<String, String>> swaggerRedirect() {
        return ResponseEntity.ok(Map.of(
                "message", "API-dokumentation finns på /swagger-ui.html",
                "redirect", "/swagger-ui.html"
        ));
    }
}