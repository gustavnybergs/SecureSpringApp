package se.secure.springapp.securespringapp.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
 * Filter som lägger till säkerhetsheaders på alla HTTP-responses
 * Körs för varje request och säkerställer att rätt headers alltid sätts
 * Skyddar mot XSS, clickjacking, MIME-sniffing och andra attacker
 */
@Component
public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Content Security Policy - förhindrar XSS och kod-injektion
        // default-src 'self' = endast vår egen domän som standard
        // script-src 'self' 'unsafe-inline' = scripts från vår domän + inline (för utveckling)
        httpResponse.setHeader("Content-Security-Policy",
                "default-src 'self'; " +
                        "script-src 'self' 'unsafe-inline'; " +
                        "style-src 'self' 'unsafe-inline'; " +
                        "img-src 'self' data:; " +
                        "font-src 'self'");

        // X-Content-Type-Options - förhindrar MIME-sniffing attacker
        // nosniff = browsern får inte gissa content-type utan måste följa vad vi säger
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");

        // X-Frame-Options - skyddar mot clickjacking
        // DENY = vår sida får aldrig visas i en iframe
        httpResponse.setHeader("X-Frame-Options", "DENY");

        // X-XSS-Protection - aktiverar browserens inbyggda XSS-skydd
        // 1; mode=block = aktiverat och blocka istället för att försöka filtrera
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");

        // Referrer-Policy - kontrollerar hur mycket referrer-info som skickas
        // strict-origin-when-cross-origin = skicka bara origin vid cross-origin requests
        httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        // Permissions-Policy - begränsar vilka browser-features som får användas
        // Stänger av kamera, mikrofon, location etc som vi inte behöver
        httpResponse.setHeader("Permissions-Policy",
                "camera=(), " +
                        "microphone=(), " +
                        "location=(), " +
                        "usb=(), " +
                        "payment=()");

        // Strict-Transport-Security - tvingar HTTPS (aktiveras bara över HTTPS)
        // max-age=31536000 = 1 år, includeSubDomains = gäller subdomäner också
        httpResponse.setHeader("Strict-Transport-Security",
                "max-age=31536000; includeSubDomains");

        // Cross-Origin-Embedder-Policy - skyddar mot vissa typer av cross-origin attacker
        httpResponse.setHeader("Cross-Origin-Embedder-Policy", "require-corp");

        // Cross-Origin-Opener-Policy - förhindrar att andra sidor får referenser till vårt fönster
        httpResponse.setHeader("Cross-Origin-Opener-Policy", "same-origin");

        // Cross-Origin-Resource-Policy - begränsar vem som får ladda våra resurser
        httpResponse.setHeader("Cross-Origin-Resource-Policy", "same-origin");

        // Fortsätt med filter-kedjan
        chain.doFilter(request, response);
    }
}