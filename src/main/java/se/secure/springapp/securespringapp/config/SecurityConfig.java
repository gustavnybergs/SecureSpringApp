package se.secure.springapp.securespringapp.config;

// TODO: Lägg till JwtAuthFilter när Utvecklare 1 har skapat den
// import se.secure.springapp.securespringapp.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import java.time.Duration;
// TODO: Lägg till UsernamePasswordAuthenticationFilter import när JWT implementeras
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Huvudkonfiguration för Spring Security i applikationen.
 * Här definierar jag vilka endpoints som kräver autentisering, vilka roller
 * som behövs för olika delar av API:et och vilka säkerhetsheaders som ska användas.
 *
 * Jag förbereder även för JWT-autentisering som Utvecklare 1 ska implementera.
 * Just nu är det mesta kommenterat bort tills JWT-delen är klar.
 *
 * @author Utvecklare 3
 * @version 1.0
 * @since 2025-06-09
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // TODO: Lägg till JwtAuthFilter när Utvecklare 1 har skapat den
    // private final JwtAuthFilter jwtAuthFilter;

    // TODO: Lägg till konstruktor när JwtAuthFilter är implementerad
    // @Autowired
    // public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
    //     this.jwtAuthFilter = jwtAuthFilter;
    // }

    /**
     * Konfigurerar säkerhetsfilterkedjan som avgör vem som får komma åt vad.
     * Här sätter jag upp rollbaserad åtkomstkontroll och säkerhetsheaders.
     *
     * Publika endpoints (som Swagger och autentisering) får alla komma åt,
     * medan admin-endpoints kräver ADMIN-roll och user-endpoints kräver USER eller ADMIN.
     *
     * @param http HttpSecurity-objekt för att konfigurera säkerhetsinställningar
     * @return komplett SecurityFilterChain med all säkerhetskonfiguration
     * @throws Exception om konfigurationen misslyckas av någon anledning
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Deaktiverar CSRF eftersom vi kommer använda JWT (stateless)
                .csrf(csrf -> csrf.disable())

                // Gör API:et stateless - ingen session-hantering
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Konfigurerar säkerhetsheaders med korrekt syntax
                .headers(headers -> headers
                        // Cache-Control headers för säker caching
                        .cacheControl(cache -> cache.disable())

                        // Frame options - förhindrar clickjacking
                        .frameOptions(frame -> frame.deny())

                        // Content type options - förhindrar MIME-sniffing
                        .contentTypeOptions(contentType -> {})

                        // XSS Protection
                        .xssProtection(xss -> {})

                        // HTTP Strict Transport Security (HSTS) - uppdaterad utan includeSubdomains
                        .httpStrictTransportSecurity(hsts -> hsts
                                .maxAgeInSeconds(Duration.ofDays(365).toSeconds())) // 1 år, utan subdomains

                        // Referrer Policy - ny syntax utan deprecated metod
                        .addHeaderWriter(new ReferrerPolicyHeaderWriter(
                                ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                )

                // Auktorisering med Lambda-syntax
                .authorizeHttpRequests(auth -> auth
                        // Publika endpoints - ingen autentisering krävs
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()

                        // Swagger dokumentation - tillgänglig för alla
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-resources/**", "/webjars/**").permitAll()
                        .requestMatchers("/swagger-ui/index.html", "/swagger-ui/favicon-32x32.png").permitAll()

                        // USER endpoints - kräver USER eller ADMIN roll
                        .requestMatchers("/api/user/**").hasAnyAuthority("USER", "ADMIN")

                        // ADMIN endpoints - kräver ADMIN roll
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")

                        // Alla andra requests kräver autentisering
                        .anyRequest().authenticated()
                )

                // TODO: Lägg till JWT-filter när Utvecklare 1 har implementerat det
                // .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    /**
     * Skapar BCrypt-lösenordskryptering för säker hantering av lösenord.
     * BCrypt är branschstandard och mycket säkrare än att lagra lösenord i klartext.
     * Den har inbyggt salt och kan konfigurera hur "dyr" krypteringen ska vara.
     *
     * @return PasswordEncoder med BCrypt-algoritm för lösenordshashing
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}