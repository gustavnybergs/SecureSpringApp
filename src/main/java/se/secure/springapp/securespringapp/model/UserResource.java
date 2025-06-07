package se.secure.springapp.securespringapp.model;

import jakarta.persistence.*;
import se.secure.springapp.securespringapp.model.User;

@Entity
@Table(name = "user_resources")
public class UserResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    // Standard konstruktor för JPA
    public UserResource() {
    }

    // Konstruktor för enklare skapande
    public UserResource(String name, String content, User owner) {
        this.name = name;
        this.content = content;
        this.owner = owner;
    }

    // Getters och setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
