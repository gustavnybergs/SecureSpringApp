package se.secure.springapp.securespringapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Request DTO för användarinloggning via REST API.
 *
 * Klassen validerar endast format och grundläggande krav på inkommande data:
 * - Email-format måste vara giltigt (test@example.com fungerar, testemail fungerar inte)
 * - Email får inte vara tomt eller null
 * - Lösenord får inte vara tomt eller null
 *
 * OBS: Denna klass kontrollerar INTE om email/lösenord är korrekta mot databasen.
 * Faktisk autentisering sker senare i AuthController efter att formatet validerats.
 *
 * Innehåller email och lösenord med automatisk validering via Bean Validation.
 * Swagger-annotationer gör att denna syns korrekt i API-dokumentationen.
 * Lombok-annotationer genererar automatiskt getters, setters och konstruktorer.
 *
 * @author Gustav
 * @version 2.0
 * @since 2025-06-09
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password") // Exkluderar lösenord från toString av säkerhetsskäl
@Schema(description = "Request för att logga in en användare")
public class LoginRequest {

    /**
     * SWAGGER-DOKUMENTATION: Beskriver email-fältet i Swagger UI
     * VALIDERING: Kontrollerar att email har rätt format och inte är tom
     * JSON-MAPPNING: Spring konverterar automatiskt JSON ↔ objekt via Lombok
     */
    @NotBlank(message = "Email får inte vara tom")
    @Email(message = "Email måste vara giltig")
    @Schema(description = "Användarens email-adress", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    /**
     * SWAGGER-DOKUMENTATION: Beskriver lösenord-fältet
     * VALIDERING: Kontrollerar att lösenord inte är tomt
     * SÄKERHET: Exkluderas från toString() för att förhindra loggning
     * JSON-MAPPNING: Spring konverterar automatiskt JSON ↔ objekt via Lombok
     */
    @NotBlank(message = "Lösenord får inte vara tomt")
    @Schema(description = "Användarens lösenord", example = "SecurePassword123!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}