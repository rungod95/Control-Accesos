package com.mina.accesos.integration;

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
}
