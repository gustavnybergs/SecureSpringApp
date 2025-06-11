package se.secure.springapp.securespringapp.model;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private final Key key;
    private final long jwtExpirationInMs;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long jwtExpirationInMs
    ) {
        if (secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret key måste vara minst 32 tecken för HS256");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    public String generateToken(Long userId, String username, Set<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("username", username)
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getUserIdFromJWT(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = parseClaims(token);
        return claims.get("username", String.class);
    }

    /**
     * Ny metod för att hämta roller från JWT-token
     */
    public Set<String> getRolesFromJWT(String token) {
        Claims claims = parseClaims(token);
        // Rollen sparas som Collection (List eller Set), vi konverterar till Set<String>
        List<String> roles = claims.get("roles", List.class);
        return roles.stream().collect(Collectors.toSet());
    }

    /**
     * Validera JWT och logga vid fel.
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("Ogiltig JWT-token: {}", e.getMessage());
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
