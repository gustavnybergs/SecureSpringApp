package se.secure.springapp.securespringapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import java.time.Duration;

/**
 * Huvudkonfiguration för Spring Security i applikationen.
 * Här definierar jag vilka endpoints som kräver autentisering, vilka roller
 * som behövs för olika delar av API:et och vilka säkerhetsheaders som ska användas.
 *
 * Utvecklare 2 (Jawhar) kommer att lägga till JWT-autentisering senare.
 * Jag har förberett strukturen med rollbaserad åtkomstkontroll.
 *
 * @author Utvecklare 3 (säkerhetsheaders), Utvecklare 2 (JWT kommer senare)
 * @version 1.0
 * @since 2025-06-09
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * Konfigurerar säkerhetsfilterkedjan som avgör vem som får komma åt vad.
     * Här sätter jag upp rollbaserad åtkomstkontroll och säkerhetsheaders.
     * JWT-filter kommer att läggas till av Utvecklare 2 senare.
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
                // Deaktiverar CSRF eftersom vi använder JWT (stateless och token-baserad autentisering)
                .csrf(csrf -> csrf.disable())

                // Gör API:et stateless - ingen session-hantering behövs med JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Konfigurerar säkerhetsheaders för skydd mot vanliga attacker
                .headers(headers -> headers
                        // Cache-Control headers för säker caching
                        .cacheControl(cache -> cache.disable())

                        // Frame options - förhindrar clickjacking
                        .frameOptions(frame -> frame.deny())

                        // Content type options - förhindrar MIME-sniffing
                        .contentTypeOptions(contentType -> {})

                        // XSS Protection
                        .xssProtection(xss -> {})

                        // HTTP Strict Transport Security (HSTS) - tvingar HTTPS
                        .httpStrictTransportSecurity(hsts -> hsts
                                .maxAgeInSeconds(Duration.ofDays(365).toSeconds())) // 1 år

                        // Referrer Policy - kontrollerar vilken referrer-information som skickas
                        .addHeaderWriter(new ReferrerPolicyHeaderWriter(
                                ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                )

                // Auktorisering med rollbaserad åtkomstkontroll
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

                        // ADMIN endpoints - kräver ADMIN roll
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")

                        // Alla andra requests kräver autentisering
                        .anyRequest().authenticated()
                )

                // TODO: JWT-filter kommer här när Utvecklare 2 implementerar det
                // .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    /**
     * Skapar BCrypt-lösenordskryptering för säker hantering av lösenord.
     * BCrypt är en av de säkraste algoritmerna för lösenordshashing och
     * rekommenderas av OWASP för produktion.
     *
     * @return PasswordEncoder-implementation med BCrypt-algoritm för lösenordskryptering
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // TODO: AuthenticationManager kommer här när Utvecklare 2 implementerar JWT
}