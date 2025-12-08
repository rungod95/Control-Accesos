package com.mina.accesos.service;

import com.mina.accesos.domain.AccessLog;
import com.mina.accesos.domain.UserAccount;
import com.mina.accesos.dto.AccessByTypeResponse;
import com.mina.accesos.dto.AccessSummaryResponse;
import com.mina.accesos.dto.VisitScanResponse;
import com.mina.accesos.exception.NotFoundException;
import com.mina.accesos.repository.AccessLogRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AccessLogService {

    private final AccessLogRepository repository;
    private final UserAccountService userAccountService;

    public AccessLogService(AccessLogRepository repository, UserAccountService userAccountService) {
        this.repository = repository;
        this.userAccountService = userAccountService;
    }

    public List<AccessLog> findAll() {
        return repository.findAll();
    }

    public AccessLog findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("AccessLog no encontrado: " + id));
    }

    public AccessLog create(AccessLog accessLog) {
        if (accessLog.getFechaHoraEntrada() == null) {
            accessLog.setFechaHoraEntrada(LocalDateTime.now());
        }
        return repository.save(accessLog);
    }

    public AccessLog update(Long id, AccessLog update) {
        AccessLog current = findById(id);
        current.setNombrePersona(update.getNombrePersona());
        current.setTipoUsuario(update.getTipoUsuario());
        current.setMotivo(update.getMotivo());
        current.setFechaHoraEntrada(update.getFechaHoraEntrada());
        current.setFechaHoraSalida(update.getFechaHoraSalida());
        current.setQrCode(update.getQrCode());
        return repository.save(current);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<AccessLog> findActive() {
        return repository.findByFechaHoraSalidaIsNullOrderByFechaHoraEntradaDesc();
    }

    public List<AccessLog> findRecent(int limit) {
        int size = Math.max(1, Math.min(100, limit));
        return repository.findAll(PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "fechaHoraEntrada")))
                .getContent();
    }

    public List<AccessLog> search(LocalDateTime desde, LocalDateTime hasta, String tipoUsuario, String qrCode) {
        LocalDateTime from = desde != null ? desde : LocalDate.now().minusYears(10).atStartOfDay();
        LocalDateTime to = hasta != null ? hasta : LocalDate.now().plusYears(10).atTime(23, 59, 59);
        String normalizedTipo = StringUtils.hasText(tipoUsuario) ? tipoUsuario.toLowerCase(Locale.ROOT) : "";
        String normalizedQr = StringUtils.hasText(qrCode) ? qrCode.trim() : "";
        return repository.search(from, to, normalizedTipo, normalizedQr);
    }

    public AccessSummaryResponse summarize() {
        long total = repository.count();
        long activos = repository.countByFechaHoraSalidaIsNull();

        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        long hoy = repository.countByFechaHoraEntradaBetween(startOfToday, now);

        LocalDateTime sevenDaysAgo = today.minusDays(6).atStartOfDay();
        long ultimaSemana = repository.countByFechaHoraEntradaBetween(sevenDaysAgo, now);

        List<AccessByTypeResponse> porTipo = repository.countGroupByTipoUsuario().stream()
                .map(tuple -> new AccessByTypeResponse(
                        tuple[0] != null ? tuple[0].toString() : "desconocido",
                        ((Number) tuple[1]).longValue()))
                .sorted((a, b) -> a.tipoUsuario().compareToIgnoreCase(b.tipoUsuario()))
                .collect(Collectors.toList());

        return new AccessSummaryResponse(total, activos, hoy, ultimaSemana, porTipo);
    }

    public VisitScanResponse scanVisitor(String qrCode) {
        if (!StringUtils.hasText(qrCode)) {
            throw new IllegalArgumentException("QR no válido.");
        }
        String normalizedQr = qrCode.trim().toUpperCase(Locale.ROOT);
        UserAccount visitor = userAccountService.findByQrCode(normalizedQr);

        AccessLog openLog = repository.findFirstByQrCodeAndFechaHoraSalidaIsNullOrderByFechaHoraEntradaDesc(normalizedQr)
                .orElse(null);

        if (openLog != null) {
            openLog.setFechaHoraSalida(LocalDateTime.now());
            repository.save(openLog);
            return VisitScanResponse.exit(visitor.getFullName(), normalizedQr, openLog.getFechaHoraEntrada(),
                    openLog.getFechaHoraSalida());
        }

        AccessLog log = new AccessLog();
        log.setNombrePersona(StringUtils.hasText(visitor.getFullName()) ? visitor.getFullName() : visitor.getUsername());
        log.setTipoUsuario(visitor.getRole().name().toLowerCase(Locale.ROOT));
        log.setMotivo("Acceso visitante QR");
        log.setQrCode(normalizedQr);
        log.setFechaHoraEntrada(LocalDateTime.now());
        repository.save(log);
        return VisitScanResponse.entry(visitor.getFullName(), normalizedQr, log.getFechaHoraEntrada());
    }
}
