package se.secure.springapp.securespringapp.model;

import jakarta.persistence.*;
import se.secure.springapp.securespringapp.model.User;

/**
 * JPA-entitet som representerar användarspecifika resurser i systemet.
 * Jag skapade denna för att hantera data som tillhör specifika användare
 * och som ska skyddas av rollbaserad åtkomstkontroll.
 *
 * Varje resurs är kopplad till en ägare (User) och kan bara kommas åt
 * av den användaren eller av administratörer. Detta implementerar
 * data-nivå säkerhet där användare bara kan se sina egna resurser.
 *
 * Exempel på användning: dokument, filer, anteckningar, inställningar etc.
 *
 * @author Gustav
 * @version 1.0
 * @since 2025-06-09
 */
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

    /**
     * Standard konstruktor för JPA.
     * Krävs av Hibernate för att skapa entiteter från databasrader.
     */
    public UserResource() {
    }

    /**
     * Konstruktor för att skapa en ny användarresurs.
     * Praktisk för att skapa resurser programmatiskt.
     *
     * @param name namnet på resursen
     * @param content innehållet i resursen (max 1000 tecken)
     * @param owner användaren som äger denna resurs
     */
    public UserResource(String name, String content, User owner) {
        this.name = name;
        this.content = content;
        this.owner = owner;
    }

    /**
     * Hämtar resursens unika ID från databasen.
     *
     * @return resurs-ID som Long, eller null för nya resurser
     */
    public Long getId() {
        return id;
    }

    /**
     * Sätter resursens ID (används normalt bara av JPA).
     *
     * @param id det nya resurs-ID:t
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Hämtar resursens namn.
     *
     * @return namnet som String
     */
    public String getName() {
        return name;
    }

    /**
     * Sätter resursens namn.
     *
     * @param name det nya namnet för resursen
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Hämtar resursens innehåll.
     *
     * @return innehållet som String (max 1000 tecken)
     */
    public String getContent() {
        return content;
    }

    /**
     * Sätter resursens innehåll.
     *
     * @param content det nya innehållet (max 1000 tecken)
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Hämtar användaren som äger denna resurs.
     *
     * @return ägaren som User-objekt
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Sätter ägaren för denna resurs.
     * Används för att ändra ägarskap eller när resursen skapas.
     *
     * @param owner den nya ägaren som User-objekt
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }
}