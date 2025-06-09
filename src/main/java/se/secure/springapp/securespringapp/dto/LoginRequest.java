package se.secure.springapp.securespringapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO för användarinloggning
 * Används för API-dokumentation och validering
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

    // Constructors
    public LoginRequest() {}

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}