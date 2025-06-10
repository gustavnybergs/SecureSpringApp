package se.secure.springapp.securespringapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

/**
 * JPA-entitet som representerar en användare i systemet.
 * Jag skapade denna så vi kan lagra användardata säkert
 * i databasen och hantera autentisering samt rollbaserade behörigheter.
 *
 * Varje användare har ett unikt användarnamn, hashat lösenord och en uppsättning
 * roller som bestämmer vad de får göra i systemet. Eager loading av roller
 * säkerställer att säkerhetskontroller alltid har tillgång till aktuella behörigheter.
 *
 * Lösenord ska alltid hashas med BCrypt innan de sparas - detta görs av
 * AuthService som Utvecklare 1 implementerar.
 *
 * @author Utvecklare 3
 * @version 1.0
 * @since 2025-06-09
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Användarnamn får inte vara tomt")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Lösenord får inte vara tomt")
    @Column(nullable = false)
    private String password;

    /**
     * Set med roller som användaren har.
     * Eager loading säkerställer att roller alltid laddas när User hämtas,
     * vilket är viktigt för säkerhetskontroller som inte kan vänta på lazy loading.
     *
     * Rollerna lagras i separat tabell (user_roles) för normalisering.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();

    @Column(name = "consent_given", nullable = false)
    private boolean consentGiven = false;

    /**
     * Standard konstruktor för JPA.
     * Krävs av Hibernate för att skapa entiteter från databasrader.
     */
    public User() {
        // Tom konstruktor för JPA
    }

    /**
     * Skapar en ny användare med grundläggande USER-roll.
     * Använd denna konstruktor när du registrerar nya användare.
     *
     * @param username användarnamnet (måste vara unikt)
     * @param password lösenordet (bör vara hashat med BCrypt innan detta anrop)
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.roles.add(Role.USER); // Alla nya användare får USER-rollen som standard
    }

    /**
     * Hämtar användarens unika ID från databasen.
     *
     * @return användar-ID som Long, eller null för nya användare
     */
    public Long getId() {
        return id;
    }

    /**
     * Sätter användarens ID (används normalt bara av JPA).
     *
     * @param id det nya användar-ID:t
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Hämtar användarens unika användarnamn.
     *
     * @return användarnamnet som String
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sätter användarens användarnamn.
     *
     * @param username det nya användarnamnet (måste vara unikt)
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Hämtar användarens hashade lösenord.
     *
     * @return det hashade lösenordet som String
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sätter användarens lösenord.
     *
     * @param password det nya lösenordet (bör vara hashat med BCrypt)
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Hämtar alla roller som användaren har.
     *
     * @return en Set med Role-enums som användaren har behörighet för
     */
    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * Sätter alla roller för användaren.
     *
     * @param roles en Set med nya roller
     */
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    /**
     * Kontrollerar om användaren har gett sitt samtycke.
     *
     * @return true om samtycke är givet, false annars
     */
    public boolean isConsentGiven() {
        return consentGiven;
    }

    /**
     * Sätter användarens samtyckesstatus.
     *
     * @param consentGiven true om användaren ger sitt samtycke
     */
    public void setConsentGiven(boolean consentGiven) {
        this.consentGiven = consentGiven;
    }

    /**
     * Lägger till en ny roll för användaren.
     * Praktisk metod för att utöka användarens behörigheter.
     *
     * @param role rollen som ska läggas till
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }

    /**
     * Tar bort en roll från användaren.
     * Praktisk metod för att minska användarens behörigheter.
     *
     * @param role rollen som ska tas bort
     */
    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    /**
     * Kontrollerar om användaren har en specifik roll.
     * Användbar för manuella behörighetskontroller i business logic.
     *
     * @param role rollen som ska kontrolleras
     * @return true om användaren har rollen, false annars
     */
    public boolean hasRole(Role role) {
        return this.roles.contains(role);
    }
}