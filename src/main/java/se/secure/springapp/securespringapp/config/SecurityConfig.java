package se.secure.springapp.securespringapp.config;

import se.secure.springapp.securespringapp.security.JwtAuthenticationFilter;
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

import java.time.Duration;

/**
 * Huvudkonfiguration för Spring Security i applikationen.
 * Kombinerar Jawhar's JWT-autentisering med Utvecklare 3's säkerhetsheaders.
 *
 * Här definieras rollbaserad åtkomstkontroll, JWT-integration och säkerhetsheaders
 * för skydd mot vanliga webbattacker som clickjacking, XSS och MIME-sniffing.
 *
 * @author Jawhar (JWT-autentisering), Utvecklare 3 (säkerhetsheaders)
 * @version 2.0 - Kombinerad implementation
 * @since 2025-06-11
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Aktiverar @PreAuthorize och @PostAuthorize
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Konfigurerar säkerhetsfilterkedjan med rollbaserade regler, JWT-filter och säkerhetsheaders.
     *
     * Kombinerar Jawhar's JWT-implementation med omfattande säkerhetsheaders för produktionsmiljö.
     * Publika endpoints (Swagger, autentisering) tillåts utan autentisering,
     * medan admin/user-endpoints kräver respektive roller.
     *
     * @param http HttpSecurity-objektet för att konfigurera säkerhetsinställningar
     * @return En komplett SecurityFilterChain med JWT-autentisering och säkerhetsheaders
     * @throws Exception om konfigurationen misslyckas
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Deaktiverar CSRF eftersom vi använder JWT (stateless och token-baserad autentisering)
                .csrf(csrf -> csrf.disable())

                // Gör API:et stateless - ingen session-hantering behövs med JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Säkerhetsheaders för skydd mot vanliga webbattacker - Utvecklare 3's implementation
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

                // Rollbaserad åtkomstkontroll - kombinerar båda implementationerna
                .authorizeHttpRequests(auth -> auth
                        // Publika endpoints - ingen autentisering krävs
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()

                        // Swagger dokumentation - tillgänglig för alla under utveckling
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-resources/**", "/webjars/**").permitAll()
                        .requestMatchers("/swagger-ui/index.html", "/swagger-ui/favicon-32x32.png").permitAll()

                        // USER endpoints - kräver USER eller ADMIN roll
                        .requestMatchers("/api/user/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers("/api/notes/**").hasAnyAuthority("USER", "ADMIN") // Jawhar's NoteController

                        // ADMIN endpoints - kräver ADMIN roll
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")

                        // Alla andra requests kräver autentisering
                        .anyRequest().authenticated()
                )

                // JWT-filtret från Jawhar - körs FÖRE Spring Securitys standardfilter
                // Detta validerar JWT-tokens och sätter upp säkerhetskontext för autentiserade användare
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    /**
     * BCrypt lösenordskryptering - säker standard för lösenordshantering.
     * Implementerat av Jawhar för säker lösenordshantering.
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
     * Implementerat av Jawhar för JWT-login-funktionalitet.
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