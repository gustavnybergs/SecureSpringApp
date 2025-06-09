package se.secure.springapp.securespringapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Konfiguration för OpenAPI/Swagger dokumentation
 * Skapar komplett API-dokumentation med säkerhetsinformation
 * Tillgänglig på /swagger-ui.html när applikationen körs
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * Huvudkonfiguration för OpenAPI dokumentation
     * Inkluderar API-info, säkerhetsscheman och servrar
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo())
                .servers(createServerList())
                .components(createComponents())
                .addSecurityItem(createSecurityRequirement());
    }

    /**
     * Skapar API-information som visas i Swagger UI
     * Inkluderar titel, beskrivning, version och kontaktinfo
     */
    private Info createApiInfo() {
        return new Info()
                .title("SecureSpringApp API")
                .description("""
                    REST API för SecureSpringApp - en säker Spring Boot applikation.
                    
                    **Funktionalitet:**
                    - Användarregistrering och autentisering med JWT
                    - Rollbaserad åtkomstkontroll (USER/ADMIN)
                    - CRUD-operationer för användarresurser
                    - Administratörsfunktioner för användarhantering
                    
                    **Säkerhetsfunktioner:**
                    - JWT-baserad autentisering
                    - Säkerhetsheaders implementerade
                    - Rate limiting för API-anrop
                    - Säkerhetsloggning av alla kritiska händelser
                    - Standardiserad felhantering
                    
                    **Roller:**
                    - **USER**: Kan hantera sina egna resurser
                    - **ADMIN**: Full tillgång till alla funktioner inklusive användarhantering
                    
                    **Autentisering:**
                    Använd `/api/auth/login` för att få en JWT-token, lägg sedan till den i Authorization header.
                    """)
                .version("1.0.0")
                .contact(createContact())
                .license(createLicense());
    }

    /**
     * Kontaktinformation för API:et
     */
    private Contact createContact() {
        return new Contact()
                .name("SecureSpringApp Team")
                .email("dev@securespringapp.se")
                .url("https://github.com/yourusername/securespringapp");
    }

    /**
     * Licensinformation för API:et
     */
    private License createLicense() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
    }

    /**
     * Konfigurerar vilka servrar som är tillgängliga
     * Automatiskt konfigurerat baserat på server.port
     */
    private List<Server> createServerList() {
        Server localServer = new Server()
                .url("http://localhost:" + serverPort)
                .description("Local development server");

        Server productionServer = new Server()
                .url("https://api.securespringapp.se")
                .description("Production server");

        return List.of(localServer, productionServer);
    }

    /**
     * Skapar säkerhetskomponenter för JWT-autentisering
     * Definierar hur JWT ska användas i API:et
     */
    private Components createComponents() {
        return new Components()
                .addSecuritySchemes("bearerAuth", createSecurityScheme());
    }

    /**
     * Definierar JWT Bearer token autentisering
     * Användare behöver inkludera "Authorization: Bearer <token>" header
     */
    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT-token erhållen från /api/auth/login endpoint");
    }

    /**
     * Kräver Bearer token för säkrade endpoints
     */
    private SecurityRequirement createSecurityRequirement() {
        return new SecurityRequirement().addList("bearerAuth");
    }
}