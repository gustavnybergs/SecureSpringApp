package se.secure.springapp.securespringapp.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import se.secure.springapp.securespringapp.service.JwtTokenProvider;

import java.io.IOException;

/**
 * JWT Authentication Filter för att validera JWT tokens i HTTP requests.
 * Elie's implementation för User Story #12 - JWT-validering, uppdaterad för att använda
 * den kombinerade JwtTokenProvider från service-paketet.
 *
 * Detta filter körs på varje HTTP request och kontrollerar om en giltig JWT token
 * finns i Authorization headern. Om token är giltig sätts användarens authentication
 * context i Spring Security för att ge tillgång till skyddade endpoints.
 *
 * Filtret hoppar över Swagger-dokumentation och andra publika endpoints för
 * att undvika onödiga valideringar på icke-skyddade resurser.
 *
 * @author Elie (Utvecklare 1) - ursprunglig implementation
 * @author Gustav & Jawhar - kombinerad JwtTokenProvider integration
 * @version 2.0 - Uppdaterad för service-baserad JWT provider
 * @since 2025-06-11
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    /**
     * Konstruktor för JwtAuthenticationFilter.
     * Injicerar beroenden för JWT-validering och användarhantering.
     *
     * @param jwtTokenProvider service för JWT token-operationer (validering, parsning)
     * @param userDetailsService service för att ladda användardetaljer från databas
     */
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Huvudmetoden som körs för varje HTTP request.
     * Extraherar JWT token från Authorization header, validerar den och sätter
     * användaren som autentiserad i SecurityContext om token är giltig.
     *
     * Hoppar över JWT-kontroll för Swagger-dokumentation och andra publika endpoints
     * för att förbättra prestanda och undvika onödiga valideringar.
     *
     * @param request HTTP request som ska filtreras
     * @param response HTTP response som skickas tillbaka
     * @param filterChain kedjan av filter som ska köras efter detta filter
     * @throws ServletException om servlet-relaterade fel uppstår
     * @throws IOException om I/O-fel uppstår under request-bearbetning
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Hoppa över JWT-kontroll för Swagger och dokumentation för bättre prestanda
        if (path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        String token = extractTokenFromHeader(header);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            authenticateUser(token, request);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extraherar JWT token från Authorization header.
     * Förväntar sig format: "Bearer <jwt-token>"
     *
     * @param authorizationHeader värdet från Authorization HTTP header
     * @return JWT token som sträng, eller null om header är ogiltig eller saknas
     */
    private String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    /**
     * Autentiserar användaren baserat på JWT token och sätter authentication context.
     * Försöker först ladda användare via userId från token, med fallback till username.
     *
     * @param token giltig JWT token som ska användas för autentisering
     * @param request HTTP request för att sätta authentication details
     */
    private void authenticateUser(String token, HttpServletRequest request) {
        try {
            String username = jwtTokenProvider.getUsername(token);
            UserDetails userDetails = loadUserDetails(token, username);

            if (userDetails != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Logga fel men fortsätt utan autentisering för graceful degradation
            System.err.println("JWT Authentication error: " + e.getMessage());
        }
    }

    /**
     * Laddar användardetaljer från token, med smart fallback-strategi.
     * Försöker först använda userId för optimal prestanda, sedan username som backup.
     *
     * @param token JWT token att extrahera användarinfo från
     * @param username användarnamn som fallback om userId inte fungerar
     * @return UserDetails objekt eller null om användare inte kan laddas
     */
    private UserDetails loadUserDetails(String token, String username) {
        // Försök med userId-baserad loading för bättre prestanda
        if (userDetailsService instanceof se.secure.springapp.securespringapp.service.UserDetailsServiceImpl) {
            try {
                Long userId = jwtTokenProvider.getUserIdFromJWT(token);
                return ((se.secure.springapp.securespringapp.service.UserDetailsServiceImpl) userDetailsService)
                        .loadUserById(userId);
            } catch (Exception e) {
                // Fallback till username-baserad loading
            }
        }

        // Standard username-baserad loading som backup
        if (username != null) {
            return userDetailsService.loadUserByUsername(username);
        }

        return null;
    }
}