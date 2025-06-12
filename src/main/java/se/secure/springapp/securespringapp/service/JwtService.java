package se.secure.springapp.securespringapp.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Service för hantering av JWT-token operationer.
 * Tillhandahåller funktionalitet för att generera och validera JSON Web Tokens.
 */
@Service
public class JwtService {

    private static final String SECRET = "superhemlignyckelsombordervara32tecken!!!";
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /**
     * Genererar en JWT-token för en autentiserad användare.
     *
     * @param authentication autentiseringsobjektet som innehåller användardetaljer
     * @return en signerad JWT-token som sträng
     */
    public String generateToken(Authentication authentication) {
        var authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .subject(authentication.getName())
                .claim("authorities", authorities)
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();
    }

    /**
     * Extraherar claims från en JWT-token.
     *
     * @param token JWT-token som ska parsas
     * @return claims som finns i token
     */
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}