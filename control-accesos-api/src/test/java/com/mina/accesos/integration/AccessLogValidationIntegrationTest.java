package com.mina.accesos.integration;

import com.mina.accesos.repository.AccessLogRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AccessLogValidationIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private AccessLogRepository accessLogRepository;

    @AfterEach
    void cleanup() {
        accessLogRepository.deleteAll();
    }

    @Test
    void createAccessLogFailsWhenRequiredFieldsMissing() throws Exception {
        String adminToken = obtainAdminToken();

        String payload = """
                {
                  "tipoUsuario":"",
                  "motivo":""
                }
                """;

        mockMvc.perform(post("/api/accesos")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validación fallida"))
                .andExpect(jsonPath("$.detalle", hasItem("nombrePersona: must not be blank")))
                .andExpect(jsonPath("$.detalle", hasItem("tipoUsuario: tipoUsuario debe ser 'trabajador', 'visitante' o 'administrador'")));
    }

    @Test
    void updateAccessLogReturnsNotFoundWhenIdDoesNotExist() throws Exception {
        String adminToken = obtainAdminToken();

        String payload = """
                {
                  "nombrePersona":"Visita Seguridad",
                  "tipoUsuario":"trabajador",
                  "motivo":"Inspeccion",
                  "fechaHoraEntrada":"2025-01-01T09:00:00",
                  "fechaHoraSalida":"2025-01-01T11:00:00",
                  "qrCode":"QR-VAL-001"
                }
                """;

        mockMvc.perform(put("/api/accesos/{id}", 999999L)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("AccessLog no encontrado: 999999"));
    }
}
