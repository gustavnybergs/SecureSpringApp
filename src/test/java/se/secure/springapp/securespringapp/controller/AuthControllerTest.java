package se.secure.springapp.securespringapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc-tester för AuthController och relaterad funktionalitet.
 * Testar autentisering, registrering och felhantering
 *
 * @author Gustav
 * @version 1.0
 * @since 2025-06-17
 */
@SpringBootTest
class AuthControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testUserRegistration_ShouldReturn201() throws Exception {
        String registerRequest = """
            {
                "username": "testuser123",
                "email": "test123@example.com",
                "password": "TestPass123!@",
                "fullName": "Test User"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Användare skapad framgångsrikt"));
    }

    @Test
    void testUserLogin_WithInvalidCredentials_ShouldReturn401() throws Exception {
        String loginRequest = """
            {
                "email": "invalid@example.com",
                "password": "wrongpassword"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isUnauthorized());
    }

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
    void testDuplicateUsername_ShouldReturn400() throws Exception {
        String registerRequest1 = """
            {
                "username": "duplicate456",
                "email": "first456@example.com",
                "password": "TestPass123!@",
                "fullName": "First User"
            }
            """;

        // Första registreringen
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequest1))
                .andExpect(status().isCreated());

        // Andra registreringen med samma username
        String registerRequest2 = """
            {
                "username": "duplicate456",
                "email": "second456@example.com", 
                "password": "TestPass123!@",
                "fullName": "Second User"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequest2))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Användarnamnet är redan taget"));
    }

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

    @Test
    void testInvalidEmailFormat_ShouldReturn400() throws Exception {
        String registerRequest = """
            {
                "username": "testuser789",
                "email": "invalid-email",
                "password": "TestPass123!@",
                "fullName": "Test User"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testShortPassword_ShouldReturn400() throws Exception {
        String registerRequest = """
            {
                "username": "testuser000",
                "email": "test000@example.com",
                "password": "123",
                "fullName": "Test User"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequest))
                .andExpect(status().isBadRequest());
    }
}