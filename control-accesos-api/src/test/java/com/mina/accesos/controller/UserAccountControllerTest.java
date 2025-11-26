package com.mina.accesos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mina.accesos.dto.AuthResponse;
import com.mina.accesos.dto.LoginRequest;
import com.mina.accesos.dto.UserAccountCreateRequest;
import com.mina.accesos.dto.UserAccountResponse;
import com.mina.accesos.dto.UserAccountUpdateRequest;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserAccountControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldCreateUpdateUserAndFetchMe() throws Exception {
        String token = obtainAdminToken();
        HttpHeaders headers = authHeaders(token);

        String username = "testuser" + ThreadLocalRandom.current().nextInt(1_000, 9_999);
        UserAccountCreateRequest createPayload = new UserAccountCreateRequest(
                username,
                "password123",
                "TRABAJADOR",
                "Test User",
                "QR-" + username.toUpperCase()
        );

        ResponseEntity<UserAccountResponse> createRes = restTemplate.exchange(
                "/api/users",
                HttpMethod.POST,
                new HttpEntity<>(createPayload, headers),
                UserAccountResponse.class);

        assertThat(createRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createRes.getBody()).isNotNull();
        assertThat(createRes.getBody().username()).isEqualTo(username);
        assertThat(createRes.getBody().qrCode()).isEqualTo("QR-" + username.toUpperCase());

        UserAccountUpdateRequest updatePayload = new UserAccountUpdateRequest(
                "TRABAJADOR",
                "Usuario Actualizado",
                null,
                "QR-" + username.toUpperCase() + "-UPDATED"
        );

        ResponseEntity<String> updateRes = restTemplate.exchange(
                "/api/users/" + createRes.getBody().id(),
                HttpMethod.PUT,
                new HttpEntity<>(updatePayload, headers),
                String.class);

        assertThat(updateRes.getStatusCode())
                .as("Update failed: %s", updateRes.getBody())
                .isEqualTo(HttpStatus.OK);
        UserAccountResponse updated = mapper.readValue(updateRes.getBody(), UserAccountResponse.class);
        assertThat(updated.fullName()).isEqualTo("Usuario Actualizado");
        assertThat(updated.qrCode()).endsWith("-UPDATED");

        ResponseEntity<UserAccountResponse> meRes = restTemplate.exchange(
                "/api/users/me",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserAccountResponse.class);

        assertThat(meRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(meRes.getBody()).isNotNull();
        assertThat(meRes.getBody().username()).isEqualTo("admin");
        assertThat(meRes.getBody().qrCode()).isNotBlank();
    }

    private String obtainAdminToken() {
        AuthResponse response = restTemplate.postForEntity(
                "/auth/login",
                new LoginRequest("admin", "admin123"),
                AuthResponse.class
        ).getBody();
        assertThat(response).isNotNull();
        return response.token();
    }

    private HttpHeaders authHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }
}
