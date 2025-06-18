package se.secure.springapp.securespringapp.entity;

import jakarta.persistence.*;

/**
 * Entitet som representerar en användare i applikationen.
 * Innehåller information om användarnamn, lösenord, roll och samtycke.
 */
@Entity
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String role;

    private boolean consentGiven;

    /**
     * Hämtar användarens unika ID.
     *
     * @return användarens ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Hämtar användarnamnet.
     *
     * @return användarnamn
     */
    public String getUsername() {
        return username;
    }

    /**
     * Hämtar användarens krypterade lösenord.
     *
     * @return lösenord
     */
    public String getPassword() {
        return password;
    }

    /**
     * Hämtar användarens roll (t.ex. USER eller ADMIN).
     *
     * @return roll
     */
    public String getRole() {
        return role;
    }

    /**
     * Indikerar om användaren har gett samtycke (t.ex. GDPR).
     *
     * @return true om samtycke givits, annars false
     */
    public boolean isConsentGiven() {
        return consentGiven;
    }



    // SETTERS

    /**
     * Setter användarens ID.
     *
     * @param id unikt användar-ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Setter användarnamnet.
     *
     * @param username användarnamn
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Setter användarens lösenord.
     *
     * @param password krypterat lösenord
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Setter användarens roll.
     *
     * @param role rollnamn
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Setter om användaren har gett samtycke.
     *
     * @param consentGiven true om samtycke givits, annars false
     */
    public void setConsentGiven(boolean consentGiven) {
        this.consentGiven = consentGiven;
    }


}
