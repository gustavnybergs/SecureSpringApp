package se.secure.springapp.securespringapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import se.secure.springapp.securespringapp.entity.Note;
import se.secure.springapp.securespringapp.service.NoteService;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public List<Note> getMyNotes(Authentication auth) {
        return noteService.getUserNotes(auth.getName());
    }

    @PostMapping
    public Note createNote(@RequestBody Note note, Authentication auth) {
        return noteService.createNote(auth.getName(), note);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Long id, Authentication auth) {
        noteService.deleteNote(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
