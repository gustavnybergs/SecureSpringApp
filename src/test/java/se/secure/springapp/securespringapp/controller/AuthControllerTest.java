package se.secure.springapp.securespringapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc-tester för AuthController.
 * Fokuserar på grundläggande funktionalitet som krävs.
 *
 * @author Gustav
 * @version 2.0
 * @since 2025-06-19
 */
@SpringBootTest
@AutoConfigureWebMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AuthControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    // ===== REGISTRERINGSTESTER =====

    @Test
    void testUserRegistration_WithValidData_ShouldReturn201() throws Exception {
        String uniqueId = String.valueOf(System.currentTimeMillis());
        String registerRequest = String.format("""
            {
                "username": "validuser%s",
                "email": "validuser%s@example.com",
                "password": "ValidPass123!@",
                "fullName": "Valid User",
                "consentGiven": true
            }
            """, uniqueId, uniqueId);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequest))
                .andExpect(status().isCreated());
    }

    @Test
    void testUserRegistration_WithoutConsent_ShouldReturn400() throws Exception {
        String uniqueId = String.valueOf(System.currentTimeMillis() + 1);
        String registerRequest = String.format("""
            {
                "username": "noconsent%s",
                "email": "noconsent%s@example.com", 
                "password": "TestPass123!@",
                "fullName": "No Consent User",
                "consentGiven": false
            }
            """, uniqueId, uniqueId);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUserRegistration_WithShortPassword_ShouldReturn400() throws Exception {
        String uniqueId = String.valueOf(System.currentTimeMillis() + 2);
        String registerRequest = String.format("""
            {
                "username": "shortpass%s",
                "email": "shortpass%s@example.com",
                "password": "short",
                "fullName": "Short Pass User",
                "consentGiven": true
            }
            """, uniqueId, uniqueId);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUserRegistration_WithInvalidEmail_ShouldReturn400() throws Exception {
        String uniqueId = String.valueOf(System.currentTimeMillis() + 3);
        String registerRequest = String.format("""
            {
                "username": "invalidemail%s",
                "email": "not-a-valid-email",
                "password": "TestPass123!@",
                "fullName": "Invalid Email User", 
                "consentGiven": true
            }
            """, uniqueId);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequest))
                .andExpect(status().isBadRequest());
    }

    // ===== INLOGGNINGSTESTER =====

    @Test
    void testUserLogin_WithInvalidCredentials_ShouldReturn401() throws Exception {
        String loginRequest = """
            {
                "email": "nonexistent@example.com",
                "password": "wrongpassword"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUserLogin_WithMissingPassword_ShouldReturn401() throws Exception {
        String loginRequest = """
            {
                "email": "testuser@example.com"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isUnauthorized());
    }

    // ===== ÅTKOMSTTESTER =====

    @Test
    void testAdminEndpoint_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/admin/hello"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUserEndpoint_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/user/hello"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUserDeletion_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(delete("/api/user/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testAdminEndpoint_WithAdminAuth_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/admin/hello"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUserEndpoint_WithUserAuth_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/user/hello"))
                .andExpect(status().isOk());
    }

    // ===== JWT TOKEN VALIDERING =====

    @Test
    void testInvalidTokenValidation_ShouldReturnFalse() throws Exception {
        String validateRequest = """
            {
                "token": "invalid.jwt.token"
            }
            """;

        mockMvc.perform(post("/api/auth/validate-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    void testMissingTokenValidation_ShouldReturn400() throws Exception {
        String validateRequest = """
            {
            }
            """;

        mockMvc.perform(post("/api/auth/validate-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validateRequest))
                .andExpect(status().isBadRequest());
    }
}