package se.secure.springapp.securespringapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO för användarinloggning via REST API.
 * Jag skapade denna för User Story #8 så vi kan validera inloggningsdata
 * innan den skickas till AuthController för autentisering.
 *
 * Innehåller email och lösenord med automatisk validering via Bean Validation.
 * Swagger-annotationer gör att denna syns korrekt i API-dokumentationen.
 *
 * @author Gustav
 * @version 1.0
 * @since 2025-06-09
 */
@Schema(description = "Request för att logga in en användare")
public class LoginRequest {

    @Schema(description = "Användarens email-adress", example = "user@example.com", required = true)
    @Email(message = "Email måste vara giltig")
    @NotBlank(message = "Email får inte vara tom")
    private String email;

    @Schema(description = "Användarens lösenord", example = "SecurePassword123!", required = true)
    @NotBlank(message = "Lösenord får inte vara tomt")
    private String password;

    /**
     * Standard konstruktor för JSON deserialisering.
     * Spring använder denna när den konverterar JSON till objekt.
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
     * Hämtar användarens email-adress.
     *
     * @return email-adressen som String
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sätter användarens email-adress.
     *
     * @param email den nya email-adressen
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Hämtar användarens lösenord.
     *
     * @return lösenordet som String
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sätter användarens lösenord.
     *
     * @param password det nya lösenordet
     */
    public void setPassword(String password) {
        this.password = password;
    }
}