package se.secure.springapp.securespringapp.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter som automatiskt lägger till säkerhetsheaders på alla HTTP-svar.
 * Jag skapade detta för User Story #4 eftersom vi behövde skydda oss mot
 * vanliga webbattacker som XSS och clickjacking. Filtret körs på alla requests
 * så vi slipper komma ihåg att lägga till headers manuellt överallt.
 *
 * Det här är viktigt för säkerheten, utan dessa headers är vi
 * sårbara för massa olika attacker som kan komprommettera användardata.
 *
 * @author Gustav
 * @version 1.0
 * @since 2025-06-09
 */
@Component
public class SecurityHeadersFilter implements Filter {

    /**
     * Huvudmetoden som filtrerar varje HTTP-request och lägger till säkerhetsheaders.
     * Spring anropar denna automatiskt för varje request som kommer in till appen.
     * Jag lade till alla viktiga headers som vi behöver enligt OWASP-rekommendationer.
     *
     * @param request inkommande HTTP-request från klienten
     * @param response HTTP-response som skickas tillbaka (här lägger vi till headers)
     * @param chain filterkedjan för att fortsätta bearbeta requesten
     * @throws IOException om något går fel med I/O
     * @throws ServletException om servlet-problem uppstår
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Förhindra MIME-sniffing attacker
        addSecurityHeader(httpResponse, "X-Content-Type-Options", "nosniff");

        // Skydda mot clickjacking attacker
        addSecurityHeader(httpResponse, "X-Frame-Options", "DENY");

        // Aktivera XSS-skydd i äldre webbläsare
        addSecurityHeader(httpResponse, "X-XSS-Protection", "1; mode=block");

        // Content Security Policy - begränsa resursladdning
        addSecurityHeader(httpResponse, "Content-Security-Policy",
                "default-src 'self'; " +
                        "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                        "style-src 'self' 'unsafe-inline'; " +
                        "img-src 'self' data:; " +
                        "font-src 'self'; " +
                        "connect-src 'self'; " +
                        "frame-ancestors 'none'"
        );

        // Kontrollera referrer-information
        addSecurityHeader(httpResponse, "Referrer-Policy", "strict-origin-when-cross-origin");

        // Begränsa webbläsarfunktioner (Permissions Policy)
        addSecurityHeader(httpResponse, "Permissions-Policy",
                "camera=(), microphone=(), geolocation=(), payment=()"
        );

        // Fortsätt med request-kedjan
        chain.doFilter(request, response);
    }

    /**
     * Hjälpmetod som lägger till en header om den inte redan finns.
     * Jag gjorde denna för att undvika dubbletter och för att validera input.
     * Kastar exception om man försöker lägga till tomma eller null-värden.
     *
     * @param response HTTP-response där headern ska läggas till
     * @param headerName vad headern heter (t.ex. "X-Frame-Options")
     * @param headerValue värdet för headern (t.ex. "DENY")
     * @throws IllegalArgumentException om headerName eller headerValue är tom/null
     */
    private void addSecurityHeader(HttpServletResponse response, String headerName, String headerValue) {
        if (headerName == null || headerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Header name cannot be null or empty");
        }
        if (headerValue == null || headerValue.trim().isEmpty()) {
            throw new IllegalArgumentException("Header value cannot be null or empty");
        }

        // Lägger bara till header om den inte redan finns
        if (response.getHeader(headerName) == null) {
            response.setHeader(headerName, headerValue);
        }
    }
}