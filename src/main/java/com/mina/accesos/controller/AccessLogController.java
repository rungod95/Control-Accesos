package com.mina.accesos.controller;

import com.mina.accesos.domain.AccessLog;
import com.mina.accesos.dto.AccessSummaryResponse;
import com.mina.accesos.service.AccessLogService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/accesos")
@CrossOrigin(origins = "*")
public class AccessLogController {

    private final AccessLogService service;

    public AccessLogController(AccessLogService service) {
        this.service = service;
    }

    @GetMapping
    public List<AccessLog> findAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @RequestParam(required = false) String tipoUsuario,
            @RequestParam(required = false) String qr) {
        if (desde != null || hasta != null || tipoUsuario != null || qr != null) {
            return service.search(desde, hasta, tipoUsuario, qr);
        }
        return service.findAll();
    }

    @GetMapping("/{id}")
    public AccessLog findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/activos")
    public List<AccessLog> activos() {
        return service.findActive();
    }

    @GetMapping("/ultimos")
    public List<AccessLog> ultimos(@RequestParam(defaultValue = "10") int limit) {
        return service.findRecent(limit);
    }

    @GetMapping("/estadisticas")
    public AccessSummaryResponse resumen() {
        return service.summarize();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccessLog create(@Valid @RequestBody AccessLog accessLog) {
        return service.create(accessLog);
    }

    @PutMapping("/{id}")
    public AccessLog update(@PathVariable Long id, @Valid @RequestBody AccessLog accessLog) {
        return service.update(id, accessLog);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
