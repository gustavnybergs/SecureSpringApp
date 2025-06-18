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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;
import se.secure.springapp.securespringapp.model.User;
import se.secure.springapp.securespringapp.repository.UserRepository;
import se.secure.springapp.securespringapp.service.UserService;

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
     * Service för användarhantering används för registrering.
     */
    private final UserService userService;

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
     * @param userService Service för användarhantering
     */
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
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
     * Implementerad av Elie, uppdaterad för Controller → Service → Repository arkitektur.
     * Delegerar validering, lösenordshashing och sparande till UserService som kontrollerar
     * dubbletter, hashar lösenord och sparar användare med standardroll USER.
     * Returnerar 201 Created med användardata (exklusive lösenord).
     *
     * @param request registreringsdata inklusive username, email och lösenord
     * @return bekräftelsemeddelande med användar-ID vid framgång
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
            User savedUser = userService.registerUser(request);

            return ResponseEntity.status(201).body(Map.of(
                    "message", "Användare skapad framgångsrikt",
                    "userId", savedUser.getId(),
                    "username", savedUser.getUsername(),
                    "email", savedUser.getEmail(),
                    "role", "USER"
            ));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Ett internt fel uppstod vid registrering",
                    "details", ex.getMessage()
            ));
        }
    }

    /**
     * Validerar JWT-token från request body.
     *
     * Tar emot en JWT-token i request body och validerar den mot
     * systemets säkerhetsinställningar. Returnerar användarinformation
     * om token är giltig.
     *
     * @param request Map innehållande "token" nyckel med JWT-token som värde
     * @return ResponseEntity med validationsresultat och användarinfo om giltig
     * @throws RuntimeException om token-validering misslyckas
     */
    @PostMapping("/validate-token")
    @Operation(
            summary = "Validera JWT-token",
            description = "Kontrollerar om den angivna JWT-token är giltig och returnerar användarinformation."
    )
    public ResponseEntity<?> validateToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "JWT token som ska valideras",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(
                                    example = """
                                {
                                    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMCIsInVzZXJuYW1lIjoiZWxpZUBleGFtcGxlLmNvbSIsInJvbGVzIjpbIlVTRVIiXSwiaWF0IjoxNzUwMDc0NzYyLCJleHAiOjE3NTAxNjExNjJ9.nsZChw5_Yksuw-s-5fg9GvZCdfqkMajnBREcW3saeMI"
                                }
                                """
                            )
                    )
            )
            @RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            if (token == null) {
                return ResponseEntity.badRequest().body(Map.of("valid", false, "error", "Token saknas"));
            }

            // Ta bort Bearer prefix om det finns
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            boolean isValid = jwtTokenProvider.validateToken(token);

            if (isValid) {
                String username = jwtTokenProvider.getUsername(token);
                return ResponseEntity.ok(Map.of("valid", true, "username", username));
            } else {
                return ResponseEntity.ok(Map.of("valid", false, "message", "Token ogiltig"));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "error", e.getMessage()));
        }
    }
}