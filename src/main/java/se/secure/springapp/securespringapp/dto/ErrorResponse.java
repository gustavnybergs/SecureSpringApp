package se.secure.springapp.securespringapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * DTO-klass för att representera felmeddelanden i API-svar.
 * Jag gjorde denna för att ha ett konsekvent format för alla fel som
 * returneras från vårt API. Innehåller status, meddelande och tidsstämpel.
 *
 * @author Utvecklare 3
 * @version 1.0
 * @since 2025-06-09
 */

public class ErrorResponse {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private int status;
    private String error;
    private String message;
    private String path;

    /**
     * Standardkonstruktor som sätter tidsstämpel automatiskt.
     */
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Konstruktor för att skapa komplett felsvar med alla detaljer.
     *
     * @param status HTTP-statuskod (t.ex. 404, 500)
     * @param message felmeddelande för användaren
     * @param error felkod för att identifiera typen av fel
     */
    public ErrorResponse(int status, String error, String message, String path) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    // Getters och setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}