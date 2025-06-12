package se.secure.springapp.securespringapp.controller;

import se.secure.springapp.securespringapp.dto.LoginRequest;
import se.secure.springapp.securespringapp.dto.RegisterRequest;
import se.secure.springapp.securespringapp.dto.ErrorResponse;
import se.secure.springapp.securespringapp.service.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import se.secure.springapp.securespringapp.model.Role;
import se.secure.springapp.securespringapp.model.User;
import se.secure.springapp.securespringapp.repository.UserRepository;

import java.util.Map;

/**
 * Controller för hantering av användarregistrering och inloggning.
 *
 * Denna klass kombinerar implementationer från Elie (registrering) och
 * Jawhar (JWT-autentisering). Endpoints är skyddade av Spring Security
 * och dokumenterade med OpenAPI/Swagger.
 *
 * @author Elie
 * @author Jawhar
 * @version 1.2
 * @since 2025-06-12
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints för användarautentisering och JWT-hantering")
public class AuthController {

    /**
     * Repository för användardata - används av Elie's registreringslogik.
     */
    private final UserRepository userRepository;

    /**
     * BCrypt encoder för lösenordshashing.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Konstruktor med dependency injection för registreringskomponenter.
     *
     * @param userRepository repository för användaroperationer
     * @param passwordEncoder BCrypt encoder för lösenord
     */
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Spring Security manager för autentisering - används av Jawhars login.
     */
    @Autowired
    private AuthenticationManager authManager;

    /**
     * JWT provider för token-generering och validering.
     */
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * Autentiserar användare och returnerar JWT-token.
     *
     * Kombinerar Jawhars JWT-logik med uppdaterad JwtTokenProvider.
     * Använder email istället för username för inloggning.
     *
     * @param request innehåller email och lösenord
     * @return JWT-token som sträng
     */
    @PostMapping("/login")
    @Operation(
            summary = "Logga in användare",
            description = "Autentiserar användare med användarnamn/email och lösenord. Returnerar JWT-token vid framgång."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Inloggning lyckad - JWT-token returnerad",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string"),
                            examples = @ExampleObject(
                                    name = "Framgångsrik inloggning",
                                    value = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Ogiltiga användaruppgifter",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Fel användaruppgifter",
                                    value = "{\"error\": \"Invalid username or password\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Felaktig begäran - saknas obligatoriska fält",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Saknas fält",
                                    value = "{\"error\": \"Username and password are required\"}"
                            )
                    )
            )
    })
    public ResponseEntity<String> login(
            @Parameter(
                    description = "Användaruppgifter för inloggning",
                    required = true,
                    schema = @Schema(implementation = LoginRequest.class)
            )
            @RequestBody LoginRequest request
    ) {
        // Jawhars autentiseringslogik med email istället för username
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = jwtTokenProvider.generateToken((UserDetails) authentication.getPrincipal());

        return ResponseEntity.ok(token);
    }

    /**
     * Registrerar ny användare i systemet.
     *
     * Implementerad av Elie. Kontrollerar dubbletter, hashar lösenord
     * och sparar användare med standardroll USER. Returnerar 201 Created
     * med användardata (exklusive lösenord).
     *
     * @param request registreringsdata inklusive username, email och lösenord
     * @return bekräftelsemeddelande med användar-ID
     */
    @PostMapping("/register")
    @Operation(
            summary = "Registrera ny användare",
            description = """
        Skapar ett nytt användarkonto med email och lösenord. 
        
        **Standard-roll:** USER
        
        **Krav:**
        - Email måste vara giltig och unik
        - Lösenord måste vara minst 8 tecken
        - Fullständigt namn är valfritt
        
        **Returvärde:**
        Bekräftelsemeddelande med användar-ID vid framgång.
        """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Användare skapad framgångsrikt",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = """
                {
                    "message": "Användare skapad framgångsrikt",
                    "userId": 123,
                    "email": "user@example.com",
                    "role": "USER"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Felaktig input eller användare finns redan",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Serverfel vid registrering",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        try {
            // Elies implementation - kontrollera dubbletter
            if (userRepository.existsByUsername(request.getUsername())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Användarnamnet är redan taget"
                ));
            }

            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Email-adressen är redan registrerad"
                ));
            }

            // Kryptera lösenord och skapa användare
            String hashedPassword = passwordEncoder.encode(request.getPassword());

            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(hashedPassword);
            newUser.setFullName(request.getFullName());
            newUser.addRole(Role.USER);
            newUser.setConsentGiven(false);

            User savedUser = userRepository.save(newUser);

            return ResponseEntity.status(201).body(Map.of(
                    "message", "Användare skapad framgångsrikt",
                    "userId", savedUser.getId(),
                    "username", savedUser.getUsername(),
                    "email", savedUser.getEmail(),
                    "role", "USER"
            ));

        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Ett internt fel uppstod vid registrering",
                    "details", ex.getMessage()
            ));
        }
    }

    /**
     * Validerar JWT-token från Authorization header.
     *
     * Endpoint skyddat av JWT-filter som automatiskt validerar token.
     * Om denna metod anropas har token redan validerats av Spring Security.
     *
     * @return bekräftelse att token är giltig
     */
    @PostMapping("/validate-token")
    @Operation(
            summary = "Validera JWT-token",
            description = "Kontrollerar om den angivna JWT-token är giltig och returnerar användarinformation."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token är giltig",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Giltig token",
                                    value = "{\"message\": \"Token validation endpoint - Använder SecurityContext från JWT-filter\", \"valid\": true}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Ogiltig eller utgången token",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Ogiltig token",
                                    value = "{\"error\": \"Invalid or expired token\"}"
                            )
                    )
            )
    })
    public ResponseEntity<?> validateToken() {
        return ResponseEntity.ok(Map.of(
                "message", "Token validation endpoint - Använder SecurityContext från JWT-filter",
                "valid", true
        ));
    }
}