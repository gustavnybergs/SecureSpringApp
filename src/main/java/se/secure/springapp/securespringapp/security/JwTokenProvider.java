package se.secure.springapp.securespringapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hjälpklass för att hantera JWT (JSON Web Tokens).
 * Ansvarar för att generera, validera och extrahera information från JWT-tokens.
 */
@Component
public class JwtTokenProvider {

    // En säker nyckel som genereras slumpmässigt vid start.
    // I en produktionsmiljö bör denna hämtas från en säker källa (t.ex. miljövariabel).
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Tokenens giltighetstid i millisekunder (här: 1 dag)
    private final long expirationMs = 86400000; // 24 * 60 * 60 * 1000 = 1 dag

    /**
     * Genererar en JWT-token för en given användare.
     * Tokenen innehåller användarnamn som 'subject' och användarens roller som en 'claim'.
     *
     * @param userDetails Detaljer om användaren som tokenen ska genereras för.
     * @return Den genererade JWT-strängen.
     */
    public String generateToken(UserDetails userDetails) {
        // Extrahera rollerna från UserDetails och konvertera dem till en lista av strängar.
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority) // Konvertera GrantedAuthority till dess strängrepresentation (t.ex. "ADMIN", "USER")
                .collect(Collectors.toList());

        // Bygg JWT-token med Jwts.builder()
        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // Användarnamnet som "subject" för token
                .claim("roles", roles) // Lägg till roller som en anpassad "claim"
                .setIssuedAt(new Date()) // När tokenen skapades
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs)) // När tokenen går ut
                .signWith(secretKey) // Signera tokenen med den hemliga nyckeln
                .compact(); // Bygg ihop tokenen till en kompakt sträng
    }

    /**
     * Extraherar användarnamnet (subject) från en JWT-token.
     *
     * @param token Den JWT-sträng som ska parsas.
     * @return Användarnamnet från tokenen.
     */
    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extraherar listan med roller från en JWT-token.
     *
     * @param token Den JWT-sträng som ska parsas.
     * @return En lista av strängar som representerar användarens roller.
     */
    public List<String> getRoles(String token) {
        // Hämta "roles"-claimen som en lista av strängar
        return getClaims(token).get("roles", List.class);
    }

    /**
     * Validerar en JWT-token. Kontrollerar om tokenen är giltig och inte utgången.
     *
     * @param token Den JWT-sträng som ska valideras.
     * @return true om tokenen är giltig, false annars.
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token); // Försök att parsa token. Om det kastar ett undantag är den ogiltig.
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Logga felet för felsökning om du vill (t.ex. token utgången, fel signatur)
            System.err.println("JWT Validation error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Parsar och validerar JWT-token och returnerar dess claims.
     * Detta är en intern hjälpmetod.
     *
     * @param token Den JWT-sträng som ska parsas.
     * @return Claims-objektet som innehåller all information från tokenens payload.
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey) // Ange vilken nyckel tokenen är signerad med
                .build()
                .parseClaimsJws(token) // Parsa tokenen och validera signaturen
                .getBody(); // Hämta "payload"-delen av tokenen
    }
}