package com.mina.accesos.service;

import com.mina.accesos.domain.AccessLog;
import com.mina.accesos.repository.AccessLogRepository;
import com.mina.accesos.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccessLogService {

    private final AccessLogRepository repository;

    public AccessLogService(AccessLogRepository repository) {
        this.repository = repository;
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
}
