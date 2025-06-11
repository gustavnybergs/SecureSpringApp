package se.secure.springapp.securespringapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.secure.springapp.securespringapp.entity.AppUser;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
}
