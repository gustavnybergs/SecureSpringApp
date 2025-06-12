package se.secure.springapp.securespringapp.service;

import org.springframework.stereotype.Service;
import se.secure.springapp.securespringapp.entity.AppUser;
import se.secure.springapp.securespringapp.exception.UserNotFoundException;
import se.secure.springapp.securespringapp.repository.AppUserRepository;

import java.util.List;

/**
 * Service för administrativa operationer relaterade till användare.
 * Hanterar hämtning och borttagning av användare.
 */
@Service
public class AdminService {

    private final AppUserRepository userRepository;

    /**
     * Konstruktor för AdminService med användarrepository.
     *
     * @param userRepository repository för användardata
     */
    public AdminService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Hämtar alla användare i systemet.
     *
     * @return lista med alla användare
     */
    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Hämtar en användare baserat på ID.
     *
     * @param id användarens ID
     * @return användaren
     * @throws UserNotFoundException om användaren inte finns
     */
    public AppUser getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    /**
     * Raderar en användare baserat på ID.
     *
     * @param id användarens ID
     * @throws UserNotFoundException om användaren inte finns
     */
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}
