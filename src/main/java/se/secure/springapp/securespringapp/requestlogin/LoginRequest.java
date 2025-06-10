package se.secure.springapp.securespringapp.requestlogin;

public class LoginRequest {
    private String username;
    private String password;

    // getters and setters

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
