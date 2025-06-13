package se.secure.springapp.securespringapp.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.secure.springapp.securespringapp.dto.TestTokenRequest;
import se.secure.springapp.securespringapp.service.JwtTokenProvider;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@Profile("dev")  // ✅ Aktiveras endast i utvecklingsmiljö
public class TestTokenController {

    private final JwtTokenProvider jwtTokenProvider;

    public TestTokenController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/test-token")
    public ResponseEntity<?> generateTestToken(@RequestBody TestTokenRequest request) {
        // ✅ Validera roller
        Set<String> roles = request.getRoles();
        for (String role : roles) {
            if (!role.equals("USER") && !role.equals("ADMIN")) {
                return ResponseEntity.badRequest().body("Ogiltig roll: " + role);
            }
        }

        // ✅ Skapa JWT-token (30 minuters giltighet)
        String token = jwtTokenProvider.generateToken(999L, request.getUsername(), roles);
        return ResponseEntity.ok().body(new TokenResponse(token));
    }

    // Enkel DTO för svar
    static class TokenResponse {
        public final String token;

        public TokenResponse(String token) {
            this.token = token;
        }
    }
}
