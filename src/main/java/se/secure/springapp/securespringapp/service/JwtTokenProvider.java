package se.secure.springapp.securespringapp.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import se.secure.springapp.securespringapp.model.UserPrincipal;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JWT Token Provider Service för att hantera JWT (JSON Web Tokens).
 * Kombinerar Jawhars struktur med Elies konfiguration.
 *
 * Denna service ansvarar för att generera, validera och extrahera information från JWT-tokens
 * för säker autentisering i REST API:et. Stödjer både enkel UserDetails-baserad generering
 * och avancerad userId/roller-baserad generering.
 *
 * @author Jawhar (struktur och dokumentation), Elie (konfiguration och avancerade features)
 * @version 2.0 - Kombinerad implementation placerad i rätt service-paket
 * @since 2025-06-11
 */
@Service
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long expirationMs;

    /**
     * Konstruktor med konfigurerbar secret och expiration från application.properties.
     * Elies förbättring för produktionsmiljö istället för hårdkodade värden.
     *
     * @param secret hemlig nyckel från application.properties (minst 32 tecken)
     * @param expirationMs token-giltighetstid i millisekunder
     * @throws IllegalArgumentException om secret är för kort för HS256
     */
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationMs) {

        if (secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret key måste vara minst 32 tecken för HS256");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    /**
     * Genererar en JWT-token för en given användare (Jawhars ursprungliga metod).
     * Tokenen innehåller användarnamn som 'subject' och användarens roller som en 'claim'.
     * Denna metod är kompatibel med standard Spring Security UserDetails.
     *
     * @param userDetails Detaljer om användaren som tokenen ska genereras för
     * @return Den genererade JWT-strängen
     */
    public String generateToken(UserDetails userDetails) {
        // Extrahera roller från UserDetails och rensa ROLE_ prefix
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", "")) // Ta bort Spring Security prefix
                .collect(Collectors.toList());

        // Försök hämta userId om UserDetails är vår UserPrincipal
        String subject = userDetails.getUsername();
        if (userDetails instanceof UserPrincipal) {
            Long userId = ((UserPrincipal) userDetails).getUserId();
            subject = userId.toString();
        }

        return Jwts.builder()
                .subject(subject)
                .claim("username", userDetails.getUsername())
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Genererar JWT-token med userId och roller (Elies metod).
     * Denna metod ger full kontroll över token-innehållet och är optimerad
     * för användning med databas-ID:n och specifika rolluppsättningar.
     *
     * @param userId användarens ID från databasen
     * @param username användarnamn/email
     * @param roles set av roller som strängar (utan ROLE_ prefix)
     * @return genererad JWT-token
     */
    public String generateToken(Long userId, String username, Set<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extraherar användarnamnet från en JWT-token.
     * Stödjer både Jawhars format (subject) och Elies format (username claim).
     *
     * @param token Den JWT-sträng som ska parsas
     * @return Användarnamnet från tokenen
     */
    public String getUsername(String token) {
        Claims claims = getClaims(token);

        // Försök först med Elies format (username som claim)
        String username = claims.get("username", String.class);
        if (username != null) {
            return username;
        }

        // Fallback till Jawhars format (username som subject)
        return claims.getSubject();
    }

    /**
     * Extraherar userId från JWT-token (Elies metod).
     * Använder subject-fältet som innehåller userId för tokens skapade med metoden.
     *
     * @param token JWT-token att parsa
     * @return användarens ID som Long
     * @throws NumberFormatException om subject inte är ett giltigt nummer
     */
    public Long getUserIdFromJWT(String token) {
        Claims claims = getClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * Extraherar listan med roller från en JWT-token (Jawhars metod).
     * Returnerar roller som en lista av strängar för kompatibilitet med Spring Security.
     *
     * @param token Den JWT-sträng som ska parsas
     * @return En lista av strängar som representerar användarens roller
     */
    public List<String> getRoles(String token) {
        return getClaims(token).get("roles", List.class);
    }

    /**
     * Extraherar roller som Set (Elies metod).
     * Användbar när du behöver en uppsättning unika roller utan dubbletter.
     *
     * @param token JWT-token att parsa
     * @return set av roller som strängar
     */
    public Set<String> getRolesFromJWT(String token) {
        Claims claims = getClaims(token);
        List<String> roles = claims.get("roles", List.class);
        return roles.stream().collect(Collectors.toSet());
    }

    /**
     * Validerar en JWT-token. Kontrollerar om tokenen är giltig och inte utgången.
     * Jawhars implementation med förbättrad felhantering.
     *
     * @param token Den JWT-sträng som ska valideras
     * @return true om tokenen är giltig, false annars
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token); // Försök att parsa token. Om det kastar undantag är den ogiltig.
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // TODO: Ersätt med proper logging när slf4j är konfigurerat
            System.err.println("JWT Validation error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Parsar och validerar JWT-token och returnerar dess claims.
     * Detta är en intern hjälpmetod som används av alla andra metoder.
     *
     * @param token Den JWT-sträng som ska parsas
     * @return Claims-objektet som innehåller all information från tokenens payload
     * @throws JwtException om token är ogiltig, utgången eller felaktigt signerad
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}