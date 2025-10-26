package com.mina.accesos.controller;

import com.mina.accesos.domain.Role;
import com.mina.accesos.domain.UserAccount;
import com.mina.accesos.dto.UserAccountCreateRequest;
import com.mina.accesos.dto.UserAccountResponse;
import com.mina.accesos.dto.UserAccountUpdateRequest;
import com.mina.accesos.service.UserAccountService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userService;

    @GetMapping
    public List<UserAccountResponse> findAll() {
        return userService.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserAccountResponse findById(@PathVariable("id") Long id) {
        return toResponse(userService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserAccountResponse create(@Valid @RequestBody UserAccountCreateRequest request) {
        Role role = parseRole(request.role());
        UserAccount created = userService.createUser(request.username(), request.password(), role, request.fullName());
        return toResponse(created);
    }

    @PutMapping("/{id}")
    public UserAccountResponse update(@PathVariable("id") Long id, @Valid @RequestBody UserAccountUpdateRequest request) {
        Role role = parseRole(request.role());
        UserAccount updated = userService.updateUser(id, request.fullName(), role, request.password());
        return toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        userService.deleteUser(id);
    }

    private Role parseRole(String role) {
        try {
            return Role.valueOf(role.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Rol no soportado: " + role);
        }
    }

    private UserAccountResponse toResponse(UserAccount user) {
        return new UserAccountResponse(user.getId(), user.getUsername(), user.getFullName(),
                user.getRole().name(), user.isEnabled());
    }
}
