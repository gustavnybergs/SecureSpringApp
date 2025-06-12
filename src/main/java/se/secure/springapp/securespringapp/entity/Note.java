package se.secure.springapp.securespringapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Entitet som representerar en användaranteckning i systemet.
 * Varje anteckning tillhör en specifik användare och innehåller titel och innehåll.
 *
 * @author Jawhar
 * @version 1.0
 * @since 2025-06-11
 */
@Entity
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private AppUser owner;

    // Getters och setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public AppUser getOwner() { return owner; }
    public void setOwner(AppUser owner) { this.owner = owner; }
}