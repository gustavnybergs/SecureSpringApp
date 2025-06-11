package se.secure.springapp.securespringapp.dto;

import se.secure.springapp.securespringapp.entity.AppUser;
import se.secure.springapp.securespringapp.model.User;

public class AppUserDTO {

    private Long id;
    private String username;
    private String role;
    private boolean consentGiven;

    public AppUserDTO() {}

    public AppUserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRoles().toString();
        this.consentGiven = user.isConsentGiven();
    }
    public AppUserDTO(AppUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole(); // om det är en sträng
        this.consentGiven = user.isConsentGiven();
    }


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
