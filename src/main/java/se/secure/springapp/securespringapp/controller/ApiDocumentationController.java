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
 * Controller för API-dokumentation och hälsokontroller
 * Tillhandahåller information om API:ets status och dokumentation
 */
@RestController
@RequestMapping("/api/public")
@Tag(name = "API Information", description = "Endpoints för API-status och dokumentation")
public class ApiDocumentationController {

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

    @Hidden  // Dölj detta endpoint från Swagger UI
    @GetMapping("/swagger-redirect")
    public ResponseEntity<Map<String, String>> swaggerRedirect() {
        return ResponseEntity.ok(Map.of(
                "message", "API-dokumentation finns på /swagger-ui.html",
                "redirect", "/swagger-ui.html"
        ));
    }
}