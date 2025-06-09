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

/*
 * Säkerhetskonfiguration för applikationen
 * Implementerar rollbaserad åtkomstkontroll och säkerhetsheaders
 * JWT-autentisering läggs till av Utvecklare 1
 *
 * Utvecklare 3: Uppdaterad för Swagger-dokumentation enligt User Story #39
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

    /*
     * Konfigurerar säkerhetsfilterkedjan med rollbaserade regler och säkerhetsheaders
     * JWT-filter läggs till senare av Utvecklare 1
     * Swagger-endpoints tillåtna för alla användare
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

    /*
     * BCrypt lösenordskryptering - säker standard för lösenordshantering
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}