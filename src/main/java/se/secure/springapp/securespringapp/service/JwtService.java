package se.secure.springapp.securespringapp.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Service för hantering av JWT-token.
 * Ansvarar för att skapa och validera JSON Web Tokens.
 */
@Service
public class JwtService {

    private static final String SECRET = "superhemlignyckelsombordervara32tecken!!!";
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /**
     * Genererar en JWT-token för en autentiserad användare.
     *
     * @param authentication autentiseringsobjekt med användarens detaljer
     * @return signerad JWT-token som sträng
     */
    public String generateToken(Authentication authentication) {
        var authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("authorities", authorities)
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 timme
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extraherar claims från en JWT-token.
     *
     * @param token JWT-token som ska parsas
     * @return claims i token
     */
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
