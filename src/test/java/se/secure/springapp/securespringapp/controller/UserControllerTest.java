package se.secure.springapp.securespringapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc-tester för UserController.
 * Fokuserar på borttagning av användare med JWT-autentisering.
 *
 * @author Gustav
 * @version 2.0
 * @since 2025-06-19
 */
@SpringBootTest
@AutoConfigureWebMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {

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

    // ===== BORTTAGNING MED JWT-AUTENTISERING =====

    @Test
    void testDeleteOwnAccount_WithValidJWT_ShouldReturn200() throws Exception {
        String uniqueId = String.valueOf(System.currentTimeMillis());

        // Steg 1: Registrera en användare
        String registerRequest = String.format("""
            {
                "username": "deleteuser%s",
                "email": "deleteuser%s@example.com",
                "password": "DeletePass123!@",
                "fullName": "Delete User",
                "consentGiven": true
            }
            """, uniqueId, uniqueId);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequest))
                .andExpect(status().isCreated());

        // Steg 2: Logga in och få JWT-token
        String loginRequest = String.format("""
            {
                "email": "deleteuser%s@example.com",
                "password": "DeletePass123!@"
            }
            """, uniqueId);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        String jwtToken = loginResult.getResponse().getContentAsString();

        // Steg 3: Ta bort eget konto med JWT-token
        mockMvc.perform(delete("/api/user/me")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteOwnAccount_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(delete("/api/user/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteOwnAccount_WithInvalidJWT_ShouldReturn401() throws Exception {
        String invalidToken = "invalid.jwt.token.here";

        mockMvc.perform(delete("/api/user/me")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteOwnAccount_WithMalformedToken_ShouldReturn401() throws Exception {
        String malformedToken = "not.a.valid.jwt";

        mockMvc.perform(delete("/api/user/me")
                        .header("Authorization", "Bearer " + malformedToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteOwnAccount_WithoutBearerPrefix_ShouldReturn401() throws Exception {
        String tokenWithoutBearer = "someTokenWithoutBearerPrefix";

        mockMvc.perform(delete("/api/user/me")
                        .header("Authorization", tokenWithoutBearer))
                .andExpect(status().isUnauthorized());
    }

    // ===== GRUNDLÄGGANDE ÅTKOMSTTESTER =====

    @Test
    void testGetUserHello_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/user/hello"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUserHello_WithValidJWT_ShouldReturn200() throws Exception {
        String uniqueId = String.valueOf(System.currentTimeMillis() + 1);

        // Registrera och logga in användare
        String registerRequest = String.format("""
            {
                "username": "hellouser%s",
                "email": "hellouser%s@example.com",
                "password": "HelloPass123!@",
                "fullName": "Hello User",
                "consentGiven": true
            }
            """, uniqueId, uniqueId);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequest))
                .andExpect(status().isCreated());

        String loginRequest = String.format("""
            {
                "email": "hellouser%s@example.com",
                "password": "HelloPass123!@"
            }
            """, uniqueId);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        String jwtToken = loginResult.getResponse().getContentAsString();

        // Testa user hello endpoint
        mockMvc.perform(get("/api/user/hello")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    // ===== EDGE CASES =====

    @Test
    void testUserEndpoints_WithEmptyAuthorizationHeader_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/user/hello")
                        .header("Authorization", ""))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUserEndpoints_WithBearerOnly_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/user/hello")
                        .header("Authorization", "Bearer "))
                .andExpect(status().isUnauthorized());
    }
}