package se.secure.springapp.securespringapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import se.secure.springapp.securespringapp.filter.SecurityHeadersFilter;

/**
 * Konfigurationsklass som registrerar vårt SecurityHeadersFilter.
 * Jag behövde denna för att Spring Boot ska veta att vårt filter ska användas.
 * Den kompletterar Spring Security:s inbyggda säkerhetsheaders med våra egna.
 *
 * @author Gustav
 * @version 1.0
 * @since 2025-06-09
 */
@Configuration
public class SecurityHeadersConfig implements WebMvcConfigurer {

    /**
     * Registrerar SecurityHeadersFilter som en Spring Bean.
     * När Spring Boot startar kommer den automatiskt att använda detta filter
     * för alla HTTP-requests. Filtret lägger då till våra extra säkerhetsheaders.
     *
     * @return SecurityHeadersFilter-instans som Spring ska använda
     */
    @Bean
    public SecurityHeadersFilter securityHeadersFilter() {
        return new SecurityHeadersFilter();
    }
}