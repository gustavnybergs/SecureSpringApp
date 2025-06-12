package se.secure.springapp.securespringapp.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import se.secure.springapp.securespringapp.entity.AppUser;
import se.secure.springapp.securespringapp.entity.Note;
import se.secure.springapp.securespringapp.repository.AppUserRepository;
import se.secure.springapp.securespringapp.repository.NoteRepository;

import java.util.List;

/**
 * Service-klass som ansvarar för hantering av anteckningar.
 * Hanterar skapande, hämtning och borttagning av användaranteckningar med korrekt auktorisering.
 */
@Service
public class NoteService {

    private final NoteRepository noteRepo;
    private final AppUserRepository userRepo;

    /**
     * Konstruktor för NoteService med nödvändiga repositories.
     *
     * @param noteRepo repository för anteckningsoperationer
     * @param userRepo repository för användaroperationer
     */
    public NoteService(NoteRepository noteRepo, AppUserRepository userRepo) {
        this.noteRepo = noteRepo;
        this.userRepo = userRepo;
    }

    /**
     * Hämtar alla anteckningar som tillhör en specifik användare.
     *
     * @param username användarnamnet för anteckningarnas ägare
     * @return en lista med anteckningar som ägs av användaren
     */
    public List<Note> getUserNotes(String username) {
        return noteRepo.findByOwnerUsername(username);
    }

    /**
     * Skapar en ny anteckning för den angivna användaren.
     *
     * @param username användarnamnet för anteckningens skapare
     * @param note anteckningen som ska skapas
     * @return den sparade anteckningen med tilldelat ID
     * @throws UsernameNotFoundException om användaren inte hittas
     */
    public Note createNote(String username, Note note) {
        AppUser user = userRepo.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found")
        );
        note.setOwner(user);
        return noteRepo.save(note);
    }

    /**
     * Raderar en anteckning om den begärande användaren är ägaren.
     *
     * @param username användarnamnet för den som begär borttagning
     * @param noteId ID:t för anteckningen som ska raderas
     * @throws RuntimeException om anteckningen inte hittas eller användaren inte är auktoriserad
     */
    public void deleteNote(String username, Long noteId) {
        Note note = noteRepo.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getOwner().getUsername().equals(username)) {
            throw new RuntimeException("Not authorized to delete this note");
        }

        noteRepo.delete(note);
    }
}
