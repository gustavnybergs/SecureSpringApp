package se.secure.springapp.securespringapp.aspect;

import se.secure.springapp.securespringapp.service.SecurityEventLogger;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/*
 * AOP Aspect som automatiskt loggar säkerhetshändelser
 * Fångar upp när metoder i controllers och services körs
 * Loggar både lyckade operationer och fel automatiskt
 */
@Aspect
@Component
public class SecurityAuditingAspect {

    private final SecurityEventLogger securityEventLogger;

    @Autowired
    public SecurityAuditingAspect(SecurityEventLogger securityEventLogger) {
        this.securityEventLogger = securityEventLogger;
    }

    /*
     * Loggar när admin-operationer körs framgångsrikt
     * Fångar alla metoder i AdminController
     */
    @AfterReturning("execution(* se.secure.springapp.securespringapp.controller.AdminController.*(..))")
    public void logAdminOperation(JoinPoint joinPoint) {
        try {
            String methodName = joinPoint.getSignature().getName();
            String username = getCurrentUsername();
            String ipAddress = getClientIpAddress();

            securityEventLogger.logSuspiciousActivity(
                    "Admin operation: " + methodName,
                    username,
                    ipAddress
            );
        } catch (Exception e) {
            // Logga inte fel i loggning för att undvika oändliga loopar
        }
    }

    /*
     * Loggar när AccessDeniedException kastas
     * Fångar alla försök att komma åt otillåtna resurser
     */
    @AfterThrowing(pointcut = "execution(* se.secure.springapp.securespringapp.controller.*.*(..))",
            throwing = "ex")
    public void logAccessDenied(JoinPoint joinPoint, AccessDeniedException ex) {
        try {
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String resource = className + "." + methodName;
            String username = getCurrentUsername();
            String ipAddress = getClientIpAddress();

            securityEventLogger.logAccessDenied(username, resource, ipAddress);
        } catch (Exception e) {
            // Logga inte fel i loggning
        }
    }

    /*
     * Loggar när users gör operationer på sina resurser
     * Hjälper att spåra normal användaraktivitet
     */
    @AfterReturning("execution(* se.secure.springapp.securespringapp.controller.UserResourceController.*(..))")
    public void logUserResourceOperation(JoinPoint joinPoint) {
        try {
            String methodName = joinPoint.getSignature().getName();
            String username = getCurrentUsername();
            String ipAddress = getClientIpAddress();

            // Logga bara kritiska operationer, inte GET-requests
            if (methodName.contains("create") || methodName.contains("update") || methodName.contains("delete")) {
                securityEventLogger.logSuspiciousActivity(
                        "User resource operation: " + methodName,
                        username,
                        ipAddress
                );
            }
        } catch (Exception e) {
            // Logga inte fel i loggning
        }
    }

    /*
     * Hjälpmetod för att få tag på användarnamnet för den inloggade användaren
     */
    private String getCurrentUsername() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                return auth.getName();
            }
            return "ANONYMOUS";
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    /*
     * Hjälpmetod för att få tag på klientens IP-adress
     * Kollar flera headers för att hantera proxies och load balancers
     */
    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                // Kolla vanliga headers för proxy/load balancer
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }

                String xRealIp = request.getHeader("X-Real-IP");
                if (xRealIp != null && !xRealIp.isEmpty()) {
                    return xRealIp;
                }

                return request.getRemoteAddr();
            }
            return "UNKNOWN";
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }
}