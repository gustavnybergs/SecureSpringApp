package se.secure.springapp.securespringapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.secure.springapp.securespringapp.entity.Note;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByOwnerUsername(String username);
}
