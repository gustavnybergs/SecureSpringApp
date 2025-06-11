package se.secure.springapp.securespringapp.controller;

import se.secure.springapp.securespringapp.requestlogin.LoginRequest;
import se.secure.springapp.securespringapp.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

/**
 * REST Controller för autentisering och användarhantering.
 * Kombinerar Jawhar's fungerande JWT-implementation med Utvecklare 3's kompletterande dokumentation.
 *
 * Hanterar inloggning, registrering och token-validering för säker åtkomst till applikationen.
 *
 * @author Jawhar (JWT-implementation), Utvecklare 3 (Swagger-dokumentation)
 * @version 2.0 - Kombinerad implementation
 * @since 2025-06-11
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints för användarautentisering och JWT-hantering")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * Autentiserar användare och returnerar JWT-token för åtkomst till skyddade endpoints.
     * Använder Jawhar's JWT-implementation med Utvecklare 3's dokumentation.
     *
     * @param request LoginRequest med användaruppgifter (username/email och password)
     * @return ResponseEntity med JWT-token vid lyckad autentisering
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
        // Jawhar's fungerande JWT-implementation
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String token = jwtTokenProvider.generateToken((UserDetails) authentication.getPrincipal());

        return ResponseEntity.ok(token);
    }

    /**
     * Validerar JWT-token och returnerar tokeninformation.
     * Användbar för frontend-applikationer för att verifiera token-giltighet.
     *
     * @return ResponseEntity med tokenvalidering och användarinfo
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
        // TODO: Implementera med SecurityContext från JWT-filter när JwtAuthenticationFilter är komplett
        // 1. Token valideras automatiskt av JWT-filter
        // 2. Om vi kommer hit är token giltig
        // 3. Returnera användarinfo från SecurityContext

        return ResponseEntity.ok(Map.of(
                "message", "Token validation endpoint - Använder SecurityContext från JWT-filter",
                "valid", true
        ));
    }

    /**
     * Registrerar ny användare i systemet.
     * Skapar användarnamn, hashad lösenord och tilldelar standardroll.
     *
     * @return ResponseEntity med registreringsresultat
     */
    @PostMapping("/register")
    @Operation(
            summary = "Registrera ny användare",
            description = "Skapar ett nytt användarkonto med användarnamn, lösenord och standardroll USER."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Användare skapad framgångsrikt",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Framgångsrik registrering",
                                    value = "{\"message\": \"User registered successfully\", \"username\": \"newuser\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Felaktig begäran eller användarnamn upptaget",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Användarnamn upptaget",
                                    value = "{\"error\": \"Username already exists\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Lösenord uppfyller inte säkerhetskrav",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Svagt lösenord",
                                    value = "{\"error\": \"Password must be at least 8 characters\"}"
                            )
                    )
            )
    })
    public ResponseEntity<?> register() {
        // TODO: Implementera registrering med användarhantering och lösenordshashing
        // 1. Validera inkommande data
        // 2. Kontrollera att användarnamn inte finns
        // 3. Hasha lösenord med BCrypt
        // 4. Spara användare med standardroll USER
        // 5. Returnera bekräftelse (inte JWT - kräv separat inloggning)

        return ResponseEntity.ok(Map.of(
                "message", "Register endpoint - Kommer implementeras med AppUser-entitet och BCrypt",
                "todo", "Väntar på användarhantering och databas"
        ));
    }
}