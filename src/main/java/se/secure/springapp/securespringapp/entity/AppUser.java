package se.secure.springapp.securespringapp.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String role;

    private boolean consentGiven;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Note> notes;
}
