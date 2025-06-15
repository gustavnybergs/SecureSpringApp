package se.secure.springapp.securespringapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO för användarregistrering via REST API.
 * Jag skapade denna för User Story #8 så vi kan validera registreringsdata
 * innan den skickas till AuthController för att skapa nya användarkonton.
 *
 * Innehåller användarnamn, email, lösenord och fullständigt namn med automatisk
 * validering via Bean Validation. Elie lade till username-validering senare.
 * Swagger-annotationer gör att denna syns korrekt i API-dokumentationen.
 *
 * Skiljer sig från LoginRequest genom att inkludera fler fält för registrering
 * medan LoginRequest bara hanterar inloggning med email och lösenord.
 *
 * @author Gustav (grundstruktur, email/password-validering), Elie (username-validering)
 * @version 1.0
 * @since 2025-06-09
 */

@Schema(description = "Request för att registrera en ny användare")
public class RegisterRequest {

    @Schema(description = "Användarens användarnamn", example = "anna123", required = true)
    @NotBlank(message = "Användarnamn får inte vara tomt")
    @Size(min = 3, max = 20, message = "Användarnamn måste vara mellan 3 och 20 tecken")
    private String username;

    @Schema(description = "Användarens email-adress", example = "user@example.com", required = true)
    @Email(message = "Email måste vara giltig")
    @NotBlank(message = "Email får inte vara tom")
    private String email;

    @Schema(description = "Lösenord för kontot", example = "SecurePassword123!", required = true, minLength = 8)
    @NotBlank(message = "Lösenord får inte vara tomt")
    @Size(min = 8, message = "Lösenord måste vara minst 8 tecken")
    private String password;

    @Schema(description = "Användarens för- och efternamn", example = "Anna Andersson")
    private String fullName;

    /**
     * Standard konstruktor för JSON deserialisering.
     * Spring använder denna när den konverterar JSON till objekt vid registrering.
     */
    public RegisterRequest() {}

    /**
     * Konstruktor för att skapa RegisterRequest med alla fält.
     * Praktisk för enhetstester och manuell objektskapning.
     *
     * @param username användarens användarnamn
     * @param email användarens email-adress
     * @param password användarens lösenord
     * @param fullName användarens fullständiga namn (optional)
     */
    public RegisterRequest(String username, String email, String password, String fullName) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

    /**
     * Hämtar användarens användarnamn.
     *
     * @return användarnamnet som String
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sätter användarens användarnamn.
     *
     * @param username det nya användarnamnet
     */
    public void setUsername(String username) {
        this.username = username;
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

    /**
     * Hämtar användarens fullständiga namn.
     *
     * @return det fullständiga namnet som String, kan vara null
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sätter användarens fullständiga namn.
     *
     * @param fullName det nya fullständiga namnet
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
