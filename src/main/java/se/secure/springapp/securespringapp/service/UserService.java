package se.secure.springapp.securespringapp.service;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import se.secure.springapp.securespringapp.model.Role;
import se.secure.springapp.securespringapp.model.User;
import se.secure.springapp.securespringapp.repository.UserRepository;

import java.util.HashSet;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(); // Alternativt injecta via konfig
    }

    public User registerUser(String username, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Användarnamnet finns redan");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRoles(new HashSet<>());
        user.addRole(Role.USER);
        user.setConsentGiven(false); // Sätt eventuellt default-värde

        return userRepository.save(user);
    }
}
