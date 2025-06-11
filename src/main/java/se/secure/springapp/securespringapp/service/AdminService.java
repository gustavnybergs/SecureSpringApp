package se.secure.springapp.securespringapp.service;

import org.springframework.stereotype.Service;
import se.secure.springapp.securespringapp.entity.AppUser;
import se.secure.springapp.securespringapp.exception.UserNotFoundException;
import se.secure.springapp.securespringapp.repository.AppUserRepository;

import java.util.List;

@Service
public class AdminService {

    private final AppUserRepository userRepository;

    public AdminService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }

    public AppUser getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}
