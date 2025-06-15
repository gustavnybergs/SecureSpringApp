package se.secure.springapp.securespringapp.controller;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.secure.springapp.securespringapp.dto.PublicInfoDTO;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @GetMapping("/app-info")
    @Cacheable("publicInfo")  //  Cachear svaret under standard cache-tid
    public ResponseEntity<PublicInfoDTO> getPublicInfo() {
        PublicInfoDTO info = new PublicInfoDTO("SecureSpringApp", "1.0.0", "Publik information för alla användare.");
        return ResponseEntity.ok(info);
    }
}
