package se.secure.springapp.securespringapp.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Enhetstester för SecurityHeadersFilter.
 * Testar att filtret lägger till rätt säkerhetsheaders på HTTP-svar.
 *
 * @author Utvecklare 3
 * @version 1.0
 * @since 2025-06-10
 */
class SecurityHeadersFilterTest {

    private SecurityHeadersFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new SecurityHeadersFilter();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
    }

    @Test
    void doFilter_ShouldAddXContentTypeOptionsHeader() throws IOException, ServletException {
        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        assertEquals("nosniff", response.getHeader("X-Content-Type-Options"));
    }

    @Test
    void doFilter_ShouldAddXFrameOptionsHeader() throws IOException, ServletException {
        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        assertEquals("DENY", response.getHeader("X-Frame-Options"));
    }

    @Test
    void doFilter_ShouldAddXXSSProtectionHeader() throws IOException, ServletException {
        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        assertEquals("1; mode=block", response.getHeader("X-XSS-Protection"));
    }

    @Test
    void doFilter_ShouldAddContentSecurityPolicyHeader() throws IOException, ServletException {
        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        String cspHeader = response.getHeader("Content-Security-Policy");
        assertNotNull(cspHeader);
        assertTrue(cspHeader.contains("default-src 'self'"));
        assertTrue(cspHeader.contains("frame-ancestors 'none'"));
    }

    @Test
    void doFilter_ShouldAddReferrerPolicyHeader() throws IOException, ServletException {
        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        assertEquals("strict-origin-when-cross-origin", response.getHeader("Referrer-Policy"));
    }

    @Test
    void doFilter_ShouldAddPermissionsPolicyHeader() throws IOException, ServletException {
        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        String permissionsHeader = response.getHeader("Permissions-Policy");
        assertNotNull(permissionsHeader);
        assertTrue(permissionsHeader.contains("camera=()"));
        assertTrue(permissionsHeader.contains("microphone=()"));
    }

    @Test
    void doFilter_ShouldNotOverwriteExistingHeaders() throws IOException, ServletException {
        // Arrange
        response.setHeader("X-Frame-Options", "EXISTING_VALUE");

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        assertEquals("EXISTING_VALUE", response.getHeader("X-Frame-Options"));
    }

    @Test
    void doFilter_ShouldContinueFilterChain() throws IOException, ServletException {
        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        assertNotNull(filterChain.getRequest());
        assertNotNull(filterChain.getResponse());
    }
}