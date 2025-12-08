package com.mina.accesos.service;

import com.mina.accesos.domain.Role;
import com.mina.accesos.domain.UserAccount;
import com.mina.accesos.dto.VisitScanResponse;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AccessLogServiceTest {

    @Autowired
    private AccessLogService accessLogService;

    @Autowired
    private UserAccountService userAccountService;

    @Test
    void shouldToggleEntryAndExitForVisitorQr() {
        String qr = "QR-VISIT-" + System.currentTimeMillis();
        UserAccount visitor = userAccountService.createUser(
                "visitante-" + System.nanoTime(),
                "password123",
                Role.VISITANTE,
                "Visita Test",
                qr
        );

        VisitScanResponse entry = accessLogService.scanVisitor(visitor.getQrCode());

        assertThat(entry).isNotNull();
        assertThat(entry.entrada()).isTrue();
        assertThat(entry.fechaHoraEntrada()).isNotNull();
        assertThat(entry.fechaHoraSalida()).isNull();

        VisitScanResponse exit = accessLogService.scanVisitor(visitor.getQrCode());

        assertThat(exit).isNotNull();
        assertThat(exit.entrada()).isFalse();
        assertThat(exit.fechaHoraEntrada()).isNotNull();
        assertThat(exit.fechaHoraSalida()).isNotNull();
    }
}
