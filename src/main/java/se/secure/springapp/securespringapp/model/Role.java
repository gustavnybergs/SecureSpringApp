package se.secure.springapp.securespringapp.model;

/**
 * Enum som definierar alla tillgängliga användarroller i systemet.
 * Jag skapade denna så vi kan implementera
 * rollbaserad åtkomstkontroll (RBAC) genom hela applikationen.
 *
 * Roller används av Spring Security för att kontrollera vem som får
 * komma åt vilka endpoints och funktioner. @PreAuthorize-annotationer
 * refererar till dessa rollnamn för behörighetskontroll.
 *
 * Alla nya användare får USER-rollen automatiskt vid registrering.
 * ADMIN-rollen ges endast manuellt av andra administratörer.
 *
 * @author Gustav
 * @version 1.0
 * @since 2025-06-09
 */
public enum Role {

    /**
     * Grundläggande användarroll som alla registrerade användare får.
     * Ger tillgång till standardfunktioner som:
     * - Visa egen profil
     * - Hantera egna resurser
     * - Använda grundläggande API-funktioner
     */
    USER,

    /**
     * Administratörsroll med utökade systemkontrollbehörigheter.
     * Ger tillgång till administrativa funktioner som:
     * - Hantera andra användare
     * - Se systemloggar och säkerhetshändelser
     * - Komma åt admin-endast endpoints
     * - Utföra systemunderhåll
     */
    ADMIN
}