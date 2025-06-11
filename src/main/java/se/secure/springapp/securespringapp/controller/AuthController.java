package se.secure.springapp.securespringapp.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import se.secure.springapp.securespringapp.dto.ErrorResponse;
import se.secure.springapp.securespringapp.dto.LoginRequest;
import se.secure.springapp.securespringapp.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import se.secure.springapp.securespringapp.model.Role;
import se.secure.springapp.securespringapp.model.User;
import se.secure.springapp.securespringapp.repository.UserRepository;

import java.util.Map;

/**
 * Controller för all användarautentisering och registrering.
 * Jag förberedde denna för User Story #8 men Utvecklare 1 ska implementera
 * själva JWT-logiken och databasintegrationerna.
 *
 * Just nu returnerar alla endpoints placeholder-svar, men jag har lagt till
 * komplett Swagger-dokumentation så vi vet exakt vad som ska implementeras.
 * Alla säkerhetsaspekter är planerade i JavaDoc och @Operation-annotationerna.
 *
 TODO: Implementeras av Utvecklare 1 - JWT och autentiseringslogik
 *
 * @author Utvecklare 3 (förberedelse), Utvecklare 1 (implementation)
 * @version 1.0
 * @since 2025-06-09
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints för användarautentisering och registrering")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registrerar nya användare i systemet med email och lösenord.
     * Utvecklare 1 ska implementera BCrypt-hashing och databasintegration.
     * Jag har förberett all validering och felhantering i Swagger-docs.
     *
     * @param request användarens registreringsdata (email, lösenord, namn)
     * @return ResponseEntity med bekräftelse eller felmeddelande
     */
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
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        try {
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
            newUser.setConsentGiven(false); // Eventuellt sätta till true om det finns samtycke

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

    @Operation(
            summary = "Logga in användare",
            description = """
            Autentiserar användare med email och lösenord. 
            
            **Process:**
            1. Validerar inloggningsuppgifter mot databas
            2. Genererar JWT-token vid lyckad autentisering
            3. Returnerar token med användarinformation
            
            **JWT-token:**
            - Giltig i 24 timmar
            - Innehåller användar-ID och roller
            - Används i Authorization header: `Bearer <token>`
            
            **Säkerhet:**
            - Lösenord hashas med BCrypt
            - Misslyckade försök loggas för säkerhetsövervakning
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Inloggning lyckad, JWT-token returnerad",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = """
                        {
                            "message": "Inloggning lyckad",
                            "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                            "user": {
                                "id": 123,
                                "email": "user@example.com",
                                "role": "USER",
                                "fullName": "Anna Andersson"
                            },
                            "expiresAt": "2024-06-09T10:32:00Z"
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Ogiltiga inloggningsuppgifter",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Serverfel vid inloggning",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @Parameter(
                    description = "Användarens inloggningsdata med email och lösenord",
                    required = true,
                    schema = @Schema(implementation = LoginRequest.class)
            )
            @Valid @RequestBody LoginRequest request) {

        // TODO: Implementeras av Utvecklare 1
        // 1. Hitta användare baserat på email
        // 2. Verifiera lösenord med BCrypt
        // 3. Generera JWT-token med användarinfo
        // 4. Logga lyckad inloggning
        // 5. Returnera token och användardata

        return ResponseEntity.ok(Map.of(
                "message", "User login endpoint - Implementeras av Utvecklare 1",
                "todo", "JWT generering och lösenordsverifiering",
                "dummyToken", "jwt-token-placeholder",
                "receivedEmail", request.getEmail()
        ));
    }

    @Operation(
            summary = "Förnya JWT-token",
            description = """
            Förnyar en giltig JWT-token med en ny med längre giltighet.
            
            **Användning:**
            - Anropas när nuvarande token snart går ut
            - Kräver giltig, icke-utgången token
            - Returnerar ny token med samma behörigheter
            
            **Säkerhet:**
            - Validerar att token är äkta och inte manipulerad
            - Kontrollerar att användaren fortfarande är aktiv
            - Gamla token invalideras automatiskt
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token förnyad framgångsrikt",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = """
                        {
                            "message": "Token förnyad framgångsrikt",
                            "newToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                            "expiresAt": "2024-06-10T10:32:00Z"
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Ogiltig eller utgången token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @Parameter(
                    description = "Authorization header med Bearer token (Bearer <token>)",
                    required = true,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader("Authorization") String authHeader) {

        // TODO: Implementeras av Utvecklare 1
        // 1. Extrahera token från Authorization header
        // 2. Validera token-signatur och giltighet
        // 3. Kontrollera att användaren fortfarande är aktiv
        // 4. Generera ny token med samma claims
        // 5. Invalidera gamla token (optional)

        return ResponseEntity.ok(Map.of(
                "message", "Token refresh endpoint - Implementeras av Utvecklare 1",
                "todo", "JWT validering och förnyelse",
                "receivedHeader", authHeader.substring(0, Math.min(50, authHeader.length())) + "..."
        ));
    }

    @Operation(
            summary = "Logga ut användare",
            description = """
            Invaliderar den aktuella JWT-token och loggar ut användaren.
            
            **Funktionalitet:**
            - Lägger till token på blacklist
            - Loggar utloggningshändelse
            - Token kan inte användas efter utloggning
            
            **Säkerhet:**
            - Förhindrar återanvändning av token
            - Säkerhetsloggning för audit trail
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Utloggning lyckad",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = """
                        {
                            "message": "Utloggning lyckad",
                            "loggedOutAt": "2024-06-08T10:32:00Z"
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Ogiltig token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(
            @Parameter(
                    description = "Authorization header med Bearer token",
                    required = true
            )
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        // TODO: Implementeras av Utvecklare 1
        // 1. Extrahera token från header
        // 2. Lägg till token på blacklist
        // 3. Logga utloggningshändelse
        // 4. Returnera bekräftelse

        return ResponseEntity.ok(Map.of(
                "message", "User logout endpoint - Implementeras av Utvecklare 1",
                "todo", "Token blacklisting och säkerhetsloggning",
                "timestamp", System.currentTimeMillis()
        ));
    }

    @Operation(
            summary = "Kontrollera token-status",
            description = """
            Validerar en JWT-token och returnerar information om dess giltighet.
            
            **Användning:**
            - Frontend kan kontrollera om token fortfarande är giltig
            - Returnerar användarinformation från token
            - Används för att uppdatera UI baserat på inloggningsstatus
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token är giltig",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = """
                        {
                            "valid": true,
                            "user": {
                                "id": 123,
                                "email": "user@example.com",
                                "role": "USER"
                            },
                            "expiresAt": "2024-06-09T10:32:00Z"
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token är ogiltig eller utgången",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken() {

        // TODO: Implementeras av Utvecklare 1
        // 1. Token valideras automatiskt av JWT-filter
        // 2. Om vi kommer hit är token giltig
        // 3. Returnera användarinfo från SecurityContext

        return ResponseEntity.ok(Map.of(
                "message", "Token validation endpoint - Implementeras av Utvecklare 1",
                "todo", "JWT validering och användardata från SecurityContext",
                "valid", true
        ));
    }
}