package se.secure.springapp.securespringapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * DTO-klass för att representera felmeddelanden i API-svar.
 * Jag gjorde denna för att ha ett konsekvent format för alla fel som
 * returneras från vårt API. Innehåller status, meddelande och tidsstämpel.
 *
 * Klassen använder Lombok för automatisk generering av getters/setters och
 * Jackson för JSON-serialisering med anpassat datumformat.
 *
 * Standard constructor sätter automatiskt timestamp till aktuell tid.
 * Används av GlobalExceptionHandler för att returnera strukturerade felmeddelanden.
 *
 * @author Gustav
 * @version 2.0
 * @since 2025-06-09
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ErrorResponse {

    /**
     * JACKSON-SERIALISERING: Formaterar datum som "yyyy-MM-dd HH:mm:ss" i JSON
     * TIDSSTÄMPEL: Automatiskt satt till aktuell tid vid objektskapande
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * HTTP-STATUSKOD: Numerisk kod som indikerar typen av fel (404, 500, etc.)
     * JSON-MAPPNING: Lombok genererar automatiskt getter/setter
     */
    private int status;

    /**
     * FELKOD: Kort beskrivning av feltypen (t.ex. "Not Found", "Internal Server Error")
     * IDENTIFIERING: Hjälper klienter att programmatiskt hantera olika feltyper
     */
    private String error;

    /**
     * FELMEDDELANDE: Detaljerad beskrivning av vad som gick fel
     * ANVÄNDARVÄNLIGT: Meddelande som kan visas direkt för slutanvändaren
     */
    private String message;

    /**
     * REQUEST-PATH: URL-sökväg där felet uppstod
     * DEBUGGING: Hjälper utvecklare att lokalisera problemet
     */
    private String path;

    /**
     * Konstruktor för att skapa komplett felsvar med alla detaljer.
     * Sätter automatiskt timestamp till aktuell tid.
     *
     * @param status HTTP-statuskod (t.ex. 404, 500)
     * @param error felkod för att identifiera typen av fel
     * @param message felmeddelande för användaren
     * @param path URL-sökväg där felet uppstod
     */
    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}