package com.mina.accesos.integration;

import com.mina.accesos.domain.Role;
import com.mina.accesos.domain.UserAccount;
import com.mina.accesos.repository.UserAccountRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserAccountIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private UserAccountRepository userAccountRepository;

    private final List<String> createdUsernames = new ArrayList<>();

    @AfterEach
    void cleanup() {
        createdUsernames.forEach(username -> userAccountRepository.findByUsername(username)
                .ifPresent(userAccountRepository::delete));
        createdUsernames.clear();
    }

    @Test
    void adminCanListUsers() throws Exception {
        String adminToken = obtainAdminToken();

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[0].username").isNotEmpty());
    }

    @Test
    void workerCannotAccessUserEndpoints() throws Exception {
        String workerToken = obtainWorkerToken();

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + workerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanCreateNewUser() throws Exception {
        String adminToken = obtainAdminToken();
        String username = "inttest_" + UUID.randomUUID().toString().substring(0, 8);
        createdUsernames.add(username);

        String payload = "{" +
                "\"username\":\"" + username + "\"," +
                "\"password\":\"pass1234\"," +
                "\"role\":\"TRABAJADOR\"," +
                "\"fullName\":\"Usuario Integracion\"" +
                "}";

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.role").value("TRABAJADOR"));

        assertThat(userAccountRepository.existsByUsername(username)).isTrue();
    }

    @Test
    void adminCanUpdateUserDetails() throws Exception {
        String adminToken = obtainAdminToken();
        String username = "inttest_update_" + UUID.randomUUID().toString().substring(0, 6);
        createdUsernames.add(username);

        UserAccount created = userAccountRepository.save(buildUser(username, "TRABAJADOR", "Nombre Inicial"));

        String payload = "{" +
                "\"role\":\"VISITANTE\"," +
                "\"fullName\":\"Nombre Actualizado\"," +
                "\"password\":\"nuevaPass123\"" +
                "}";

        mockMvc.perform(put("/api/users/{id}", created.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("VISITANTE"))
                .andExpect(jsonPath("$.fullName").value("Nombre Actualizado"));

        UserAccount reloaded = userAccountRepository.findById(created.getId()).orElseThrow();
        assertThat(reloaded.getRole().name()).isEqualTo("VISITANTE");
        assertThat(reloaded.getFullName()).isEqualTo("Nombre Actualizado");
        assertThat(reloaded.getPassword()).isNotEqualTo(created.getPassword());
    }

    @Test
    void adminCanDeleteUser() throws Exception {
        String adminToken = obtainAdminToken();
        String username = "inttest_delete_" + UUID.randomUUID().toString().substring(0, 6);

        UserAccount created = userAccountRepository.save(buildUser(username, "VISITANTE", "Eliminar"));

        mockMvc.perform(delete("/api/users/{id}", created.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        assertThat(userAccountRepository.existsById(created.getId())).isFalse();
    }

    @Test
    void workerCannotModifyUsers() throws Exception {
        String workerToken = obtainWorkerToken();
        String adminToken = obtainAdminToken();

        String username = "inttest_worker_" + UUID.randomUUID().toString().substring(0, 6);
        createdUsernames.add(username);
        UserAccount created = userAccountRepository.save(buildUser(username, "TRABAJADOR", "Cambio bloqueado"));

        String payload = "{" +
                "\"role\":\"ADMIN\"," +
                "\"fullName\":\"No deberia\"}";

        mockMvc.perform(put("/api/users/{id}", created.getId())
                        .header("Authorization", "Bearer " + workerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/users/{id}", created.getId())
                        .header("Authorization", "Bearer " + workerToken))
                .andExpect(status().isForbidden());

        // cleanup new user to avoid pollution
        mockMvc.perform(delete("/api/users/{id}", created.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void duplicateUsernameReturnsBadRequest() throws Exception {
        String adminToken = obtainAdminToken();

        String payload = "{" +
                "\"username\":\"operario1\"," +
                "\"password\":\"pass1234\"," +
                "\"role\":\"TRABAJADOR\"," +
                "\"fullName\":\"Operario Duplicado\"" +
                "}";

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ya existe un usuario con username operario1"));
    }

    @Test
    void workerCannotCreateUsers() throws Exception {
        String workerToken = obtainWorkerToken();

        String payload = "{" +
                "\"username\":\"blockedUser\"," +
                "\"password\":\"pass1234\"," +
                "\"role\":\"TRABAJADOR\"," +
                "\"fullName\":\"Bloqueado\"" +
                "}";

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + workerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isForbidden());
    }

    private UserAccount buildUser(String username, String role, String fullName) {
        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setPassword("$2a$10$abcdefghijklmno1234567890pqrstuvwxyzABCDEFXYZabcd1234"); // 60-char dummy bcrypt
        user.setRole(Role.valueOf(role));
        user.setFullName(fullName);
        user.setEnabled(true);
        return user;
    }
}
