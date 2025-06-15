package se.secure.springapp.securespringapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

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
 *
 * @author Gustav
 * @version 1.0
 * @since 2025-06-09
 */
// SWAGGER-DOKUMENTATION: Beskriver hela klassen för API-doc
@Schema(description = "Request för att logga in en användare")
public class LoginRequest {

    // SWAGGER-DOKUMENTATION: Beskriver email-fältet i Swagger UI
    @Schema(description = "Användarens email-adress", example = "user@example.com", required = true)
    // VALIDERING: Kontrollerar att email har rätt format
    @Email(message = "Email måste vara giltig")
    // VALIDERING: Kontrollerar att email inte är tom
    @NotBlank(message = "Email får inte vara tom")
    private String email; // ← STRUKTUR: Privat fält för email

    // SWAGGER-DOKUMENTATION: Beskriver lösenord-fältet
    @Schema(description = "Användarens lösenord", example = "SecurePassword123!", required = true)
    @NotBlank(message = "Lösenord får inte vara tomt") // Validering av att lösen inte är tomt
    private String password;

    /**
     * JSON-MAPPNING: Tom konstruktor för Spring att konvertera JSON → objekt
     */
    public LoginRequest() {}

    /**
     * Konstruktor för att skapa LoginRequest med alla fält.
     * Praktisk för enhetstester och manuell objektskapning.
     *
     * @param email användarens email-adress
     * @param password användarens lösenord
     */
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * JSON-MAPPNING: Getter för Spring att konvertera objekt → JSON
     * Hämtar användarens email-adress.
     *
     * @return email-adressen som String
     */
    public String getEmail() {
        return email;
    }

    /**
     * JSON-MAPPNING: Setter för Spring att sätta värden från JSON
     * Sätter användarens email-adress.
     *
     * @param email den nya email-adressen
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * JSON-MAPPNING: Getter för lösenord
     * Hämtar användarens lösenord.
     *
     * @return lösenordet som String
     */
    public String getPassword() {
        return password;
    }

    /**
     * JSON-MAPPNING: Setter för lösenord
     * Sätter användarens lösenord.
     *
     * @param password det nya lösenordet
     */
    public void setPassword(String password) {
        this.password = password;
    }
}