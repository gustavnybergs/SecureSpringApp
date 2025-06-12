package se.secure.springapp.securespringapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfigurationsklass för Swagger/OpenAPI-dokumentation av vårt REST API.
 * Jag skapade denna för User Story #5 (39 enligt github commit) eftersom vi behövde automatisk API-dokumentation
 * som utvecklare kan använda för att förstå hur vårt API fungerar.
 *
 * Swagger UI blir tillgängligt på /swagger-ui.html så man kan testa API:et direkt
 * i webbläsaren. Riktigt smidigt för utveckling och testning.
 *
 * @author Gustav
 * @version 1.0
 * @since 2025-06-09
 */
@Configuration
public class OpenApiConfig {

    /**
     * Skapar huvudkonfigurationen för OpenAPI-dokumentationen.
     * Här samlar jag ihop all metadata och säkerhetsinställningar som ska visas
     * i Swagger UI. Spring Boot genererar sedan automatiskt dokumentationen.
     *
     * @return komplett OpenAPI-konfiguration med all info som behövs
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo())
                .components(createSecurityComponents());
    }

    /**
     * Skapar grundläggande info om vårt API som visas överst i Swagger UI.
     * Här beskriver jag vad API:et gör, vilka säkerhetsfunktioner vi har
     * och hur man använder autentiseringen.
     *
     * @return Info-objekt med titel, beskrivning, version och kontaktuppgifter
     */
    private Info createApiInfo() {
        return new Info()
                .title("SecureSpringApp API")
                .description("""
                    REST API för säker webbapplikation byggt med Spring Boot.
                    
                    **Säkerhetsfunktioner:**
                    - JWT-baserad autentisering
                    - Rollbaserad åtkomstkontroll (ADMIN, USER)
                    - Säkerhetsheaders för XSS/CSRF-skydd
                    - Rate limiting för DDoS-skydd
                    - Omfattande säkerhetsloggning
                    
                    **Autentisering:**
                    API:et använder JWT Bearer tokens för autentisering.
                    Logga in via /api/auth/login för att få en token.
                    
                    **Roller:**
                    - **ADMIN**: Full åtkomst till alla endpoints
                    - **USER**: Begränsad åtkomst till användarfunktioner
                    """)
                .version("1.0.0")
                .contact(createContactInfo())
                .license(createLicenseInfo());
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

    /**
     * Skapar licensinformation för API:et.
     * Definierar under vilka villkor API:et får användas och distribueras.
     *
     * @return License-objekt med licensinformation
     */
    private License createLicenseInfo() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
    }

    /**
     * Skapar säkerhetskomponenter för OpenAPI-specifikationen.
     * Definierar autentiseringsscheman som används av säkra endpoints,
     * primärt JWT Bearer Token-autentisering.
     *
     * @return Components-objekt med konfigurerade säkerhetsscheman
     */
    private Components createSecurityComponents() {
        return new Components()
                .addSecuritySchemes("bearerAuth", createJwtSecurityScheme());
    }

    /**
     * Skapar JWT Bearer Token-säkerhetsschema för autentisering.
     * Definierar hur JWT-tokens ska skickas i HTTP-headers för
     * att autentisera requests till säkra endpoints.
     *
     * Tokens ska skickas som: Authorization: Bearer <jwt-token>
     *
     * @return SecurityScheme-objekt konfigurerat för JWT Bearer authentication
     */
    private SecurityScheme createJwtSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("""
                    JWT Bearer Token autentisering.
                    
                    **Så här använder du:**
                    1. Logga in via POST /api/auth/login
                    2. Kopiera JWT-token från response
                    3. Klicka på 'Authorize'-knappen ovan
                    4. Ange token (utan 'Bearer ' prefix)
                    5. Alla efterföljande requests autentiseras automatiskt
                    
                    **Token format:** Bearer <jwt-token>
                    **Header:** Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
                    """);
    }
}