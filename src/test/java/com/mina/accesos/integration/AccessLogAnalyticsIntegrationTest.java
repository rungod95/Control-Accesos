package com.mina.accesos.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.mina.accesos.domain.AccessLog;
import com.mina.accesos.repository.AccessLogRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AccessLogAnalyticsIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private AccessLogRepository accessLogRepository;

    @BeforeEach
    void setUp() {
        accessLogRepository.deleteAll();

        LocalDateTime now = LocalDateTime.now();

        AccessLog active = new AccessLog();
        active.setNombrePersona("Operario Uno");
        active.setTipoUsuario("trabajador");
        active.setMotivo("Turno tarde");
        active.setFechaHoraEntrada(now.minusHours(1));
        active.setQrCode("QR-OP-001");
        accessLogRepository.save(active);

        AccessLog closedRecent = new AccessLog();
        closedRecent.setNombrePersona("Visita Mantenimiento");
        closedRecent.setTipoUsuario("visitante");
        closedRecent.setMotivo("Revisión banda");
        closedRecent.setFechaHoraEntrada(now.minusHours(3));
        closedRecent.setFechaHoraSalida(now.minusHours(1));
        closedRecent.setQrCode("QR-VIS-002");
        accessLogRepository.save(closedRecent);

        AccessLog oldRecord = new AccessLog();
        oldRecord.setNombrePersona("Supervisor Seguridad");
        oldRecord.setTipoUsuario("trabajador");
        oldRecord.setMotivo("Inspección mensual");
        oldRecord.setFechaHoraEntrada(now.minusDays(10));
        oldRecord.setFechaHoraSalida(now.minusDays(10).plusHours(2));
        oldRecord.setQrCode("QR-SUP-003");
        accessLogRepository.save(oldRecord);
    }

    @AfterEach
    void tearDown() {
        accessLogRepository.deleteAll();
    }

    @Test
    void activosReturnsOnlyOpenEntries() throws Exception {
        String token = obtainAdminToken();

        mockMvc.perform(get("/api/accesos/activos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombrePersona").value("Operario Uno"));
    }

    @Test
    void ultimosRespectsLimitParameter() throws Exception {
        String token = obtainAdminToken();

        mockMvc.perform(get("/api/accesos/ultimos")
                        .param("limit", "2")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void filterByTipoUsuarioReturnsMatches() throws Exception {
        String token = obtainAdminToken();

        mockMvc.perform(get("/api/accesos")
                        .param("tipoUsuario", "visitante")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tipoUsuario").value("visitante"));
    }

    @Test
    void summaryEndpointReturnsAggregatedCounts() throws Exception {
        String token = obtainAdminToken();

        String payload = mockMvc.perform(get("/api/accesos/estadisticas")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(payload);
        assertThat(node.get("total").asInt()).isEqualTo(3);
        assertThat(node.get("activos").asInt()).isEqualTo(1);
        assertThat(node.get("hoy").asInt()).isEqualTo(2);
        assertThat(node.get("ultimaSemana").asInt()).isEqualTo(2);

        Map<String, Integer> porTipo = new HashMap<>();
        node.get("porTipo").forEach(entry -> porTipo.put(entry.get("tipoUsuario").asText(), entry.get("total").asInt()));
        assertThat(porTipo.get("trabajador")).isEqualTo(2);
        assertThat(porTipo.get("visitante")).isEqualTo(1);
    }
}
