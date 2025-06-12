package se.secure.springapp.securespringapp.config;

import se.secure.springapp.securespringapp.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.Arrays;

/**
 * Huvudkonfiguration för Spring Security i applikationen.
 * Kombinerar Jawhars JWT-autentisering, Gustavs säkerhetsheaders och Elies uppdateringar.
 * UPPDATERAD: Lagt till CORS-support för React frontend på port 3000.
 *
 * Här definieras rollbaserad åtkomstkontroll, JWT-integration, säkerhetsheaders
 * och CORS-konfiguration för skydd mot vanliga webbattacker.
 *
 * @author Jawhar (JWT-autentisering), Gustav (säkerhetsheaders, CORS), Elie (filter path + databas),
 * @version 4.0 - Lagt till CORS för React frontend
 * @since 2025-06-11
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Konfigurerar CORS för React frontend på port 3000.
     * FIXAD VERSION - Rättade konfigurationen för att lösa preflight-problem.
     *
     * @return CorsConfigurationSource med konfiguration för React integration
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Ändra från setAllowedOriginPatterns till setAllowedOrigins
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Konfigurerar säkerhetsfilterkedjan med rollbaserade regler, JWT-filter, säkerhetsheaders och CORS.
     *
     * Kombinerar Jawhars JWT-implementation med Gustavs säkerhetsheaders,
     * Elies uppdaterade filter-sökvägar och CORS-support för React frontend.
     *
     * @param http HttpSecurity-objektet för att konfigurera säkerhetsinställningar
     * @return komplett SecurityFilterChain med JWT-autentisering, säkerhetsheaders och CORS
     * @throws Exception om konfigurationen misslyckas
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Aktivera CORS för React frontend
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Deaktiverar CSRF eftersom vi använder JWT (stateless och token-baserad autentisering)
                .csrf(csrf -> csrf.disable())

                // Gör API:et stateless - ingen session-hantering behövs med JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Säkerhetsheaders för skydd mot vanliga webbattacker - Gustav's implementation
                .headers(headers -> headers
                        // Cache-Control headers för säker caching av känsligt innehåll
                        .cacheControl(cache -> cache.disable())

                        // Frame options - förhindrar clickjacking genom att blockera iframes
                        .frameOptions(frame -> frame.deny())

                        // Content type options - förhindrar MIME-sniffing attacker
                        .contentTypeOptions(contentType -> {})

                        // XSS Protection - aktiverar webbläsarens inbyggda XSS-filter
                        .xssProtection(xss -> {})

                        // HTTP Strict Transport Security (HSTS) - tvingar HTTPS-användning
                        .httpStrictTransportSecurity(hsts -> hsts
                                .maxAgeInSeconds(Duration.ofDays(365).toSeconds())) // 1 år

                        // Referrer Policy - kontrollerar vilken referrer-information som skickas vid länkar
                        .addHeaderWriter(new ReferrerPolicyHeaderWriter(
                                ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                )

                // Rollbaserad åtkomstkontroll - kombinerar alla implementationer
                .authorizeHttpRequests(auth -> auth
                        // Publika endpoints - ingen autentisering krävs
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()

                        // Swagger dokumentation - tillgänglig för alla under utveckling
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-resources/**", "/webjars/**").permitAll()
                        .requestMatchers("/swagger-ui/index.html", "/swagger-ui/favicon-32x32.png").permitAll()

                        // USER endpoints - kräver USER eller ADMIN roll
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/notes/**").hasAnyAuthority("USER", "ADMIN") // Jawhars NoteController

                        // ADMIN endpoints - kräver ADMIN roll
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")

                        // Alla andra requests kräver autentisering
                        .anyRequest().authenticated()
                )

                // JWT-filtret - körs FÖRE Spring Securitys standardfilter (Elies uppdaterade filter path)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    /**
     * BCrypt lösenordskryptering - säker standard för lösenordshantering.
     *
     * @return En instans av BCryptPasswordEncoder med standardstyrka (10 rounds)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Konfigurerar och exponerar AuthenticationManager som en Spring Bean.
     * Nödvändigt för att kunna utföra autentisering i AuthController.
     *
     * @param config AuthenticationConfiguration-objektet från Spring Security
     * @return En instans av AuthenticationManager för autentiseringsprocesser
     * @throws Exception om ett fel uppstår under konfigurationen
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}