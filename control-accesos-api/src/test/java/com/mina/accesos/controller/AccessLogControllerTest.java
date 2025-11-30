package com.mina.accesos.controller;

import com.mina.accesos.dto.AuthResponse;
import com.mina.accesos.dto.LoginRequest;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AccessLogControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnSummaryAndRecentAccesses() {
        HttpHeaders headers = authHeaders();

        ResponseEntity<String> summary = restTemplate.exchange(
                "/api/accesos/estadisticas",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        assertThat(summary.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(summary.getBody()).contains("total").contains("activos");

        ResponseEntity<List<?>> recent = restTemplate.exchange(
                "/api/accesos/ultimos?limit=5",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<?>>() {});

        assertThat(recent.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(recent.getBody()).isNotNull();
        assertThat(recent.getBody().size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void shouldRespectLimitAndReturnStructuredItems() {
        HttpHeaders headers = authHeaders();

        ResponseEntity<List<LinkedHashMap<String, Object>>> recent = restTemplate.exchange(
                "/api/accesos/ultimos?limit=1",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {});

        assertThat(recent.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(recent.getBody()).isNotNull().hasSize(1);
        LinkedHashMap<String, Object> item = recent.getBody().getFirst();
        assertThat(item).containsKeys("id", "nombrePersona", "tipoUsuario", "fechaHoraEntrada");
    }

    @Test
    void shouldRejectUnauthorizedRequests() {
        ResponseEntity<String> summary = restTemplate.exchange(
                "/api/accesos/estadisticas",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                String.class);
        assertThat(summary.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        ResponseEntity<String> recent = restTemplate.exchange(
                "/api/accesos/ultimos?limit=2",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                String.class);
        assertThat(recent.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private HttpHeaders authHeaders() {
        AuthResponse response = restTemplate.postForEntity(
                "/auth/login",
                new LoginRequest("admin", "admin123"),
                AuthResponse.class
        ).getBody();
        assertThat(response).isNotNull();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(response.token());
        return headers;
    }
}
