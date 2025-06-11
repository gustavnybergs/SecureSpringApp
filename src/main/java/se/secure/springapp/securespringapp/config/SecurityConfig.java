package se.secure.springapp.securespringapp.config;

import se.secure.springapp.securespringapp.filter.JwtAuthenticationFilter;  // Ändring 1: Importera JwtAuthenticationFilter
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;  // Ändring 2: Importera UsernamePasswordAuthenticationFilter för att kunna placera Jwt-filter rätt
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import java.time.Duration;

/**
 * Huvudkonfiguration för Spring Security i applikationen.
 * Här definierar vi vilka endpoints som kräver autentisering, vilka roller
 * som behövs för olika delar av API:et och vilka säkerhetsheaders som ska användas.
 *
 * Förbereder även för JWT-autentisering via JwtAuthenticationFilter.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;  // Ändring 3: Lägg till JwtAuthenticationFilter som beroende

    // Ändring 4: Konstruktor som injicerar JwtAuthenticationFilter
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Deaktiverar CSRF eftersom vi använder JWT (stateless)
                .csrf(csrf -> csrf.disable())

                // Gör API:et stateless - ingen session-hantering
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Säkerhetsheaders
                .headers(headers -> headers
                        .cacheControl(cache -> cache.disable())
                        .frameOptions(frame -> frame.deny())
                        .contentTypeOptions(contentType -> {}) // Kan anpassas vid behov
                        .xssProtection(xss -> {})              // Kan aktiveras vid behov
                        .httpStrictTransportSecurity(hsts -> hsts
                                .maxAgeInSeconds(Duration.ofDays(365).toSeconds()))
                        .addHeaderWriter(new ReferrerPolicyHeaderWriter(
                                ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                )

                // Auktorisering av endpoints
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-resources/**", "/webjars/**").permitAll()
                        .requestMatchers("/swagger-ui/index.html", "/swagger-ui/favicon-32x32.png").permitAll()
                        .requestMatchers("/api/user/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated()
                )

                // Ändring 5: Lägg till JwtAuthenticationFilter före UsernamePasswordAuthenticationFilter i filterkedjan
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
