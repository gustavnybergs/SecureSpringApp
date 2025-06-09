package se.secure.springapp.securespringapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO för användarregistrering
 * Används för API-dokumentation och validering
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

    // Constructors
    public RegisterRequest() {}

    public RegisterRequest(String email, String password, String fullName) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}