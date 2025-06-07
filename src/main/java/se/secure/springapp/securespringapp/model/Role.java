package se.secure.springapp.securespringapp.model;

/**
 * Enum som definierar tillgängliga användarroller i systemet.
 * Används för rollbaserad åtkomstkontroll.
 */
public enum Role {
    /** Grundläggande användarroll */
    USER,
    /** Administratörsroll med utökade behörigheter */
    ADMIN
}