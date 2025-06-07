package se.secure.springapp.securespringapp.config;

// TODO: Lägg till JwtAuthFilter när Elie har skapat den
// import se.secure.springapp.securespringapp.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
// TODO: Lägg till UsernamePasswordAuthenticationFilter import när JWT implementeras
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Säkerhetskonfiguration för applikationen.
 * Implementerar rollbaserad åtkomstkontroll.
 * JWT-autentisering läggs till av Elie
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
     * Konfigurerar säkerhetsfilterkedjan med rollbaserade regler.
     * JWT-filter läggs till senare av Elie
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deaktiverar CSRF eftersom vi kommer använda JWT (stateless)
                .csrf().disable()
                // Gör API:et stateless - ingen session-hantering
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
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
                .anyRequest().authenticated();
        // TODO: Lägg till JWT-filter när Utvecklare 1 har implementerat det
        // .and()
        // .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * BCrypt lösenordskryptering - säker standard för lösenordshantering.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}