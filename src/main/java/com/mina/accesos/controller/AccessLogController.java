package com.mina.accesos.controller;

import com.mina.accesos.domain.AccessLog;
import com.mina.accesos.service.AccessLogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accesos")
@CrossOrigin(origins = "*")
public class AccessLogController {

    private final AccessLogService service;

    public AccessLogController(AccessLogService service) {
        this.service = service;
    }

    @GetMapping
    public List<AccessLog> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public AccessLog findById(@PathVariable Long id) {
        return service.findById(id);
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
