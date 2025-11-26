package com.mina.accesos.controller;

import com.mina.accesos.dto.AuthResponse;
import com.mina.accesos.dto.LoginRequest;
import com.mina.accesos.dto.RefreshTokenRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldLoginWithValidCredentials() {
        LoginRequest payload = new LoginRequest("admin", "admin123");

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity("/auth/login", payload, AuthResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().token()).isNotBlank();
        assertThat(response.getBody().refreshToken()).isNotBlank();
        assertThat(response.getBody().username()).isEqualTo("admin");
        assertThat(response.getBody().role()).isEqualTo("ADMIN");
    }

    @Test
    void shouldRejectInvalidCredentials() {
        LoginRequest payload = new LoginRequest("admin", "wrong");

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity("/auth/login", payload, AuthResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldRefreshTokenWithValidRefreshToken() {
        LoginRequest payload = new LoginRequest("admin", "admin123");
        AuthResponse login = restTemplate.postForEntity("/auth/login", payload, AuthResponse.class).getBody();
        assertThat(login).isNotNull();

        RefreshTokenRequest refreshPayload = new RefreshTokenRequest(login.refreshToken());
        ResponseEntity<AuthResponse> response = restTemplate.exchange(
                "/auth/refresh",
                HttpMethod.POST,
                new HttpEntity<>(refreshPayload),
                AuthResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().token()).isNotBlank();
        assertThat(response.getBody().refreshToken()).isNotBlank();
        assertThat(response.getBody().username()).isEqualTo("admin");
    }
}
