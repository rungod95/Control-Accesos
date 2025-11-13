package com.mina.accesos.dto;

import jakarta.validation.constraints.NotBlank;

public record UserAccountCreateRequest(
        @NotBlank(message = "username es obligatorio")
        String username,
        @NotBlank(message = "password es obligatorio")
        String password,
        @NotBlank(message = "role es obligatorio")
        String role,
        String fullName
) {
}
