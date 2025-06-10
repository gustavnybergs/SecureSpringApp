package se.secure.springapp.securespringapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO för användarregistrering via REST API.
 * Jag skapade denna för User Story #8 så vi kan validera registreringsdata
 * och säkerställa att nya användare har korrekt formaterade uppgifter.
 *
 * Inkluderar email, lösenord och valfritt fullständigt namn.
 * Bean Validation kontrollerar att email är giltig och lösenord är tillräckligt starkt.
 *
 * @author Utvecklare 3
 * @version 1.0
 * @since 2025-06-09
 */
@Schema(description = "Request för att registrera en ny användare")
public class RegisterRequest {

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
     * Spring använder denna när den konverterar JSON till objekt.
     */
    public RegisterRequest() {}

    /**
     * Konstruktor för att skapa RegisterRequest med alla fält.
     * Praktisk för enhetstester och manuell objektskapning.
     *
     * @param email användarens email-adress
     * @param password användarens lösenord (minst 8 tecken)
     * @param fullName användarens fullständiga namn (valfritt)
     */
    public RegisterRequest(String email, String password, String fullName) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
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
     * @param email den nya email-adressen (måste vara giltig)
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
     * @param password det nya lösenordet (minst 8 tecken)
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Hämtar användarens fullständiga namn.
     *
     * @return det fullständiga namnet som String, eller null om inte angivet
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sätter användarens fullständiga namn.
     *
     * @param fullName det fullständiga namnet (valfritt)
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}