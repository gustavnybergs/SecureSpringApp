package se.secure.springapp.securespringapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Entitet som representerar en användaranteckning i systemet.
 * Varje anteckning är kopplad till en specifik användare.
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

    /**
     * Hämtar anteckningens ID.
     * @return anteckningens ID
     */
    public Long getId() { return id; }

    /**
     * Sätter anteckningens ID.
     * @param id ID att sätta
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Hämtar anteckningens titel.
     * @return anteckningens titel
     */
    public String getTitle() { return title; }

    /**
     * Sätter anteckningens titel.
     * @param title titel att sätta
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Hämtar anteckningens innehåll.
     * @return anteckningens innehåll
     */
    public String getContent() { return content; }

    /**
     * Sätter anteckningens innehåll.
     * @param content innehåll att sätta
     */
    public void setContent(String content) { this.content = content; }

    /**
     * Hämtar ägaren av anteckningen.
     * @return ägaren (AppUser)
     */
    public AppUser getOwner() { return owner; }

    /**
     * Sätter ägaren av anteckningen.
     * @param owner ägaren att sätta
     */
    public void setOwner(AppUser owner) { this.owner = owner; }
}
