package se.secure.springapp.securespringapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.secure.springapp.securespringapp.dto.AppUserDTO;
import se.secure.springapp.securespringapp.entity.AppUser;
import se.secure.springapp.securespringapp.service.AdminService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST-controller för administratörsspecifika operationer.
 * Endpoints i denna controller är skyddade för användare med ADMIN-roll.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    /**
     * Konstruktor som injicerar AdminService.
     *
     * @param adminService service för admin-operationer
     */
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Testendpoint för att verifiera ADMIN-access.
     *
     * @return välkomstmeddelande för admin
     */
    @GetMapping("/hello")
    public ResponseEntity<String> getAdminHello() {
        return ResponseEntity.ok("Välkommen, du är inloggad som ADMIN!");
    }

    /**
     * Hämtar alla användare i systemet som DTO:er.
     *
     * @return lista med användare i DTO-format
     */
    @GetMapping("/users")
    public ResponseEntity<List<AppUserDTO>> getAllUsers() {
        List<AppUser> users = adminService.getAllUsers();
        List<AppUserDTO> userDTOs = users.stream()
                .map(AppUserDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    /**
     * Hämtar en enskild användare baserat på ID.
     *
     * @param id användarens ID
     * @return användare i DTO-format
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<AppUserDTO> getUserById(@PathVariable Long id) {
        AppUser user = adminService.getUserById(id);
        return ResponseEntity.ok(new AppUserDTO(user));
    }

    /**
     * Raderar en användare baserat på ID.
     *
     * @param id användarens ID
     * @return 204 No Content om borttagning lyckas
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
