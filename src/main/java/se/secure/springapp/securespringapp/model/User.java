package se.secure.springapp.securespringapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

/**
 * JPA-entitet som representerar en användare i systemet.
 * Hanterar autentisering och rollbaserade behörigheter.
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
     * Eager loading för att alltid ha tillgång till roller vid säkerhetskontroller.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();

    @Column(name = "consent_given", nullable = false)
    private boolean consentGiven = false;

    // Konstruktorer
    public User() {
        // Tom konstruktor för JPA
    }

    /**
     * Skapar en ny användare med grundläggande USER-roll.
     * @param username användarnamnet
     * @param password lösenordet (bör vara hashat innan detta anrop)
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.roles.add(Role.USER); // Alla nya användare får USER-rollen som standard
    }

    // Getters och setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    public boolean isConsentGiven() { return consentGiven; }
    public void setConsentGiven(boolean consentGiven) { this.consentGiven = consentGiven; }

    /**
     * Lägger till en roll för användaren.
     * @param role rollen som ska läggas till
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }

    /**
     * Tar bort en roll från användaren.
     * @param role rollen som ska tas bort
     */
    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    /**
     * Kontrollerar om användaren har en specifik roll.
     * @param role rollen som ska kontrolleras
     * @return true om användaren har rollen, false annars
     */
    public boolean hasRole(Role role) {
        return this.roles.contains(role);
    }
}