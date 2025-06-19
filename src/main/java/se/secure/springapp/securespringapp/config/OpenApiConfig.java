package se.secure.springapp.securespringapp.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfigurationsklass för Swagger/OpenAPI-dokumentation med JWT Bearer token support.
 *
 * FÖRE: Användare måste manuellt kopiera JWT token till varje endpoint
 * EFTER: Authorize-knapp 🔒 gör alla endpoints automatiskt autentiserade
 *
 * @author Gustav (original), uppdaterad för JWT Bearer tokens
 * @version 2.0 - Bearer Token Integration
 * @since 2025-06-18
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "SecureSpringApp API",
                version = "1.0.0",
                description = "REST API för säker webbapplikation med JWT Bearer token autentisering"
        ),
        security = @SecurityRequirement(name = "bearerAuth") // ← DENNA RAD AKTIVERAR 🔒 KNAPPEN
)
@SecurityScheme(
        name = "bearerAuth",                    // Namn som refereras ovan
        type = SecuritySchemeType.HTTP,         // HTTP-baserad auth
        scheme = "bearer",                      // Bearer token format
        bearerFormat = "JWT",                   // Specificerar JWT format
        description = "JWT Bearer token från /api/auth/login endpoint. Klistra bara in token (utan 'Bearer ' prefix) Om hänglåser går från upplåst till låst så är din token validerad korrekt."
)
public class OpenApiConfig {

    /**
     * Skapar huvudkonfigurationen för OpenAPI-dokumentationen.
     * Nu med automatisk Bearer Token support för alla skyddade endpoints.
     *
     * @return komplett OpenAPI-konfiguration med JWT Bearer auth
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo());
    }

    /**
     * Skapar API-information med instruktioner för JWT Bearer tokens.
     *
     * @return Info-objekt med titel, beskrivning och Bearer Token instruktioner
     */
    private io.swagger.v3.oas.models.info.Info createApiInfo() {
        return new io.swagger.v3.oas.models.info.Info()
                .title("SecureSpringApp API")
                .description("""
                    REST API för säker webbapplikation byggt med Spring Boot.
                    
                    **🔐 Så här använder du JWT Bearer tokens:**
                    1. POST /api/auth/login med email/password
                    2. Kopiera JWT token från response body
                    3. Klicka "Authorize" 🔒 knappen ovan
                    4. Klistra in token (utan "Bearer " prefix)
                    5. Klicka "Authorize" - nu fungerar alla skyddade endpoints!
                    
                    **⚡ Endpoints som kräver autentisering:**
                    - GET /api/user/hello (USER eller ADMIN)
                    - GET /api/user/me (USER eller ADMIN) 
                    - DELETE /api/user/me (USER eller ADMIN)
                    - GET /api/admin/hello (endast ADMIN)
                    - GET /api/admin/users (endast ADMIN)
                    - DELETE /api/admin/users/{id} (endast ADMIN)
                    
                    **🛡️ Säkerhetsfunktioner:**
                    - JWT Bearer token autentisering
                    - Rollbaserad åtkomstkontroll (ADMIN, USER)
                    - BCrypt lösenordshashing
                    - Säkerhetsheaders för XSS/CSRF-skydd
                    - Omfattande säkerhetsloggning
                    """)
                .version("1.0.0")
                .contact(createContactInfo());
    }

    /**
     * Skapar kontaktinformation för API-dokumentationen.
     *
     * @return Contact-objekt med utvecklarens kontaktuppgifter
     */
    private Contact createContactInfo() {
        return new Contact()
                .name("Gustav Nyberg")
                .email("gustav.nyberg@securespringapp.se")
                .url("https://github.com/gustavnyberg");
    }
}