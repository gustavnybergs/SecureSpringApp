package se.secure.springapp.securespringapp.repository;

import se.secure.springapp.securespringapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository-gränssnitt för att hantera databasoperationer för User-entiteten.
 * Utökar JpaRepository för att få grundläggande CRUD-operationer.
 *
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Hittar en användare baserat på användarnamn.
     *
     * @param username Användarnamnet att söka efter.
     * @return En Optional som innehåller användaren om den hittas, annars tom.
     */
    Optional<User> findByUsername(String username);

    /**
     * Hittar en användare baserat på email-adress.
     *
     * @param email Email-adressen att söka efter.
     * @return En Optional som innehåller användaren om den hittas, annars tom.
     */
    Optional<User> findByEmail(String email);

    /**
     * Kontrollerar om en användare med angivet email redan finns.
     *
     * @param email Email-adressen att kontrollera.
     * @return true om email redan finns, false annars.
     */
    boolean existsByEmail(String email);

    /**
     * Kontrollerar om en användare med angivet användarnamn redan finns.
     *
     * @param username Användarnamnet att kontrollera.
     * @return true om användarnamn redan finns, false annars.
     */
    boolean existsByUsername(String username);
}