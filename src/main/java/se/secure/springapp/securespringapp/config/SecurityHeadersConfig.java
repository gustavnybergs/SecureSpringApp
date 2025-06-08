package se.secure.springapp.securespringapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
 * Konfiguration för säkerhetsheaders som skyddar mot vanliga webbattacker
 * Implementerar headers för XSS-skydd, clickjacking-skydd, MIME-sniffing etc
 * Använder både Spring Security och manuella headers för maximal säkerhet
 */
@Configuration
public class SecurityHeadersConfig implements WebMvcConfigurer {

    /*
     * Filter som lägger till extra säkerhetsheaders på alla responses
     * Kompletterar Spring Security's inbyggda headers
     */
    @Bean
    public SecurityHeadersFilter securityHeadersFilter() {
        return new SecurityHeadersFilter();
    }
}