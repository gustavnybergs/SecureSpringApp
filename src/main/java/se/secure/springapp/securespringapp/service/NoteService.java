package se.secure.springapp.securespringapp.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import se.secure.springapp.securespringapp.entity.AppUser;
import se.secure.springapp.securespringapp.entity.Note;
import se.secure.springapp.securespringapp.repository.AppUserRepository;
import se.secure.springapp.securespringapp.repository.NoteRepository;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepo;
    private final AppUserRepository userRepo;

    public NoteService(NoteRepository noteRepo, AppUserRepository userRepo) {
        this.noteRepo = noteRepo;
        this.userRepo = userRepo;
    }

    public List<Note> getUserNotes(String username) {
        return noteRepo.findByOwnerUsername(username);
    }

    public Note createNote(String username, Note note) {
        AppUser user = userRepo.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found")
        );
        note.setOwner(user);
        return noteRepo.save(note);
    }

    public void deleteNote(String username, Long noteId) throws AccessDeniedException {
        Note note = noteRepo.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("Not your note");
        }

        noteRepo.delete(note);
    }

    // Ev. updateNote, etc.
}
