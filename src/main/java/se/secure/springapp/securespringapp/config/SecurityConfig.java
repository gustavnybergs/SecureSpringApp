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

/**
 * Säkerhetskonfiguration för applikationen.
 * Implementerar rollbaserad åtkomstkontroll och JWT-autentisering.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Aktiverar pre/post-annotations, t.ex. @PreAuthorize
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Konfigurerar säkerhetsfilterkedjan med rollbaserade regler och JWT-filter.
     *
     * @param http HttpSecurity-objektet för att konfigurera säkerheten.
     * @return En konfigurerad SecurityFilterChain.
     * @throws Exception om ett fel uppstår under konfigurationen.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deaktiverar CSRF eftersom vi använder JWT (stateless och token-baserad autentisering)
                .csrf(csrf -> csrf.disable()) // Uppdaterad syntax
                // Gör API:et stateless - ingen session-hantering behövs med JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Uppdaterad syntax
                .authorizeHttpRequests(auth -> auth
                        // Publika endpoints - ingen autentisering krävs
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        // Swagger dokumentation - tillgänglig för alla
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // USER endpoints - kräver USER eller ADMIN roll
                        .requestMatchers("/api/user/**").hasAnyAuthority("USER", "ADMIN")
                        // ADMIN endpoints - kräver ADMIN roll
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                        // Alla andra requests kräver autentisering
                        .anyRequest().authenticated()
                )
                // Lägg till JWT-filtret FÖRE Spring Securitys standardfilter för användarnamn/lösenord.
                // Detta innebär att vår JWT-validering körs först.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * BCrypt lösenordskryptering - säker standard för lösenordshantering.
     *
     * @return En instans av BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Konfigurerar och exponerar AuthenticationManager som en Spring Bean.
     * Detta är nödvändigt för att kunna utföra autentisering (t.ex. vid inloggning).
     *
     * @param config AuthenticationConfiguration-objektet.
     * @return En instans av AuthenticationManager.
     * @throws Exception om ett fel uppstår under konfigurationen.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}