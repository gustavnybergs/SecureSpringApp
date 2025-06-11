package se.secure.springapp.securespringapp.controller;

import se.secure.springapp.securespringapp.dto.LoginRequest;
import se.secure.springapp.securespringapp.dto.RegisterRequest;
import se.secure.springapp.securespringapp.dto.ErrorResponse;
import se.secure.springapp.securespringapp.service.JwtTokenProvider;  // ENDA ÄNDRINGEN HÄR
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
 * REST Controller för autentisering och användarhantering.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints för användarautentisering och JWT-hantering")
public class AuthController {

    // Elie's dependency injection för registrering
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ENDA ÄNDRINGEN: Byt från jwtUtil till jwtTokenProvider
    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;  // ÄNDRAT FRÅN jwtUtil

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
        // Kombinerad JWT-implementation (Jawhar's logic + uppdaterad JwtTokenProvider)
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())  // ÄNDRAT FRÅN getUsername() till getEmail()
        );

        String token = jwtTokenProvider.generateToken((UserDetails) authentication.getPrincipal());  // ÄNDRAT FRÅN jwtUtil

        return ResponseEntity.ok(token);
    }

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
            // Elie's registrerings-implementation
            // 1. Kontrollera om användarnamn eller email redan finns
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

            // 2. Kryptera lösenord med BCrypt
            String hashedPassword = passwordEncoder.encode(request.getPassword());

            // 3. Skapa ny användare
            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(hashedPassword);
            newUser.setFullName(request.getFullName());
            newUser.addRole(Role.USER); // Tilldela standardrollen USER
            newUser.setConsentGiven(false);

            // 4. Spara till databas
            User savedUser = userRepository.save(newUser);

            // 5. Returnera 201 Created med användarinfo (utan lösenord)
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