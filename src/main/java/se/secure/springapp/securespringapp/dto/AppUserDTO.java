package se.secure.springapp.securespringapp.dto;

import se.secure.springapp.securespringapp.entity.AppUser;
import se.secure.springapp.securespringapp.model.User;

/**
 * Dataöverföringsobjekt (DTO) för användare.
 * Används för att exponera användaruppgifter utan känslig information som lösenord.
 */
public class AppUserDTO {
    private Long id;
    private String username;
    private String role;
    private boolean consentGiven;

    /**
     * Tom konstruktor som krävs av Spring för att skapa instanser.
     */
    public AppUserDTO() {
    }

    /**
     * Skapar en DTO baserat på en User-entitet.
     *
     * @param user User-entitet som ska omvandlas till DTO
     */
    public AppUserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRoles().toString();
        this.consentGiven = user.isConsentGiven();
    }

    /**
     * Skapar en DTO baserat på en AppUser-entitet.
     *
     * @param user AppUser-entitet som ska omvandlas till DTO
     */
    public AppUserDTO(AppUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole(); // om det är en sträng
        this.consentGiven = user.isConsentGiven();
    }

    // Getters

    /**
     * Hämtar användarens ID.
     *
     * @return användarens unika ID
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
     * Hämtar användarens roll.
     *
     * @return roll (t.ex. USER, ADMIN)
     */
    public String getRole() {
        return role;
    }

    /**
     * Indikerar om användaren har gett samtycke.
     *
     * @return true om samtycke givits, annars false
     */
    public boolean isConsentGiven() {
        return consentGiven;
    }

    // Setters

    /**
     * Sätter användarens ID.
     *
     * @param id unikt ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Sätter användarnamnet.
     *
     * @param username användarnamn
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sätter användarens roll.
     *
     * @param role rollnamn
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Sätter samtyckesstatus.
     *
     * @param consentGiven true om samtycke givits
     */
    public void setConsentGiven(boolean consentGiven) {
        this.consentGiven = consentGiven;
    }
}