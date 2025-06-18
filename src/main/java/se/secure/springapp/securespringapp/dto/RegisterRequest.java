package se.secure.springapp.securespringapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import jakarta.validation.constraints.Pattern;

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
 * @version 2.0
 * @since 2025-06-17
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password") // Exkluderar lösenord från toString av säkerhetsskäl
@Schema(description = "Request objekt för användarregistrering")
public class RegisterRequest {

    @NotBlank(message = "Användarnamn är obligatoriskt")
    @Size(min = 3, max = 50, message = "Användarnamn måste vara mellan 3 och 50 tecken")
    @Schema(description = "Användarens användarnamn", example = "anna123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "Email är obligatorisk")
    @Email(message = "Email måste vara i giltigt format")
    @Schema(description = "Användarens email-adress", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Lösenord är obligatoriskt")
    @Size(min = 8, message = "Lösenord måste vara minst 8 tecken långt")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d{2,})(?=.*[!@#$%&*]{2,}).{8,}$",
            message = "Lösenord måste innehålla minst 1 stor bokstav, 2 siffror och 2 specialtecken (!@#$%&*)"
    )
    @Schema(description = "Lösenord för kontot", example = "SecurePassword123!@", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 8)
    private String password;

    @NotBlank(message = "Fullständigt namn är obligatoriskt")
    @Size(min = 2, max = 100, message = "Fullständigt namn måste vara mellan 2 och 100 tecken")
    @Schema(description = "Användarens fullständiga namn", example = "Anna Andersson", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fullName;
}