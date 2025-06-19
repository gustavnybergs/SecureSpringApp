package se.secure.springapp.securespringapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc-tester för AdminController.
 * Testar admin-specifika funktioner och rollbaserad åtkomst.
 *
 * @author Gustav
 * @version 1.0
 * @since 2025-06-19
 */
@SpringBootTest
@AutoConfigureWebMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AdminControllerTest {

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

    // ===== ADMIN ÅTKOMSTTESTER =====

    @Test
    void testAdminHello_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/admin/hello"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testAdminHello_WithAdminAuth_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/admin/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("ADMIN")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testAdminHello_WithUserRole_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/admin/hello"))
                .andExpect(status().isForbidden());
    }

    // ===== ADMIN ANVÄNDARHANTERING =====

    @Test
    void testGetAllUsers_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetAllUsers_WithAdminAuth_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetAllUsers_WithUserRole_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    // ===== ADMIN BORTTAGNING AV ANVÄNDARE =====

    @Test
    void testDeleteUserById_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(delete("/api/admin/users/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testDeleteUserById_WithUserRole_ShouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/admin/users/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDeleteUserById_WithNonExistentUser_ShouldReturn404() throws Exception {
        // Testar med ett ID som troligen inte finns
        mockMvc.perform(delete("/api/admin/users/99999"))
                .andExpect(status().isNotFound());
    }

    // ===== ADMIN ANVÄNDAR-INFO =====

    @Test
    void testGetUserById_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/admin/users/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetUserById_WithNonExistentUser_ShouldReturn404() throws Exception {
        // Testar med ett ID som troligen inte finns
        mockMvc.perform(get("/api/admin/users/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetUserById_WithUserRole_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/admin/users/1"))
                .andExpect(status().isForbidden());
    }

    // ===== SÄKERHETSTESTER =====

    @Test
    void testAllAdminEndpoints_RequireAuthentication() throws Exception {
        // Testa att alla admin-endpoints kräver autentisering
        mockMvc.perform(get("/api/admin/hello")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/admin/users")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/admin/users/1")).andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/api/admin/users/1")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testAllAdminEndpoints_DenyUserRole() throws Exception {
        // Testa att vanliga användare inte kan komma åt admin-endpoints
        mockMvc.perform(get("/api/admin/hello")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/admin/users")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/admin/users/1")).andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/admin/users/1")).andExpect(status().isForbidden());
    }
}