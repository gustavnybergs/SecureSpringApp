package se.secure.springapp.securespringapp.dto;



import java.util.Set;

public class TestTokenRequest {
    private String username;
    private Set<String> roles;

    // Getters & setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}
