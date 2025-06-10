package se.secure.springapp.securespringapp.repository;

import se.secure.springapp.securespringapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository-gränssnitt för att hantera databasoperationer för User-entiteten.
 * Utökar JpaRepository för att få grundläggande CRUD-operationer.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> { // User är entiteten, Long är ID-typen

    /**
     * Hittar en användare baserat på användarnamn.
     *
     * @param username Användarnamnet att söka efter.
     * @return En Optional som innehåller användaren om den hittas, annars tom.
     */
    Optional<User> findByUsername(String username);
}