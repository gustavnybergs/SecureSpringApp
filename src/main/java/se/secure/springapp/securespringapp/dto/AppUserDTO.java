package se.secure.springapp.securespringapp.dto;


import se.secure.springapp.securespringapp.entity.AppUser;

public class AppUserDTO {

    private Long id;
    private String username;
    private String role;
    private boolean consentGiven;

    // Tom konstruktor krävs av Spring
    public AppUserDTO() {
    }

    // Används för att skapa DTO från AppUser
    public AppUserDTO(AppUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole();
        this.consentGiven = user.isConsentGiven();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public boolean isConsentGiven() {
        return consentGiven;
    }

    // Setters (också bra att ha för JSON-mappning)
    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setConsentGiven(boolean consentGiven) {
        this.consentGiven = consentGiven;
    }
}
