package com.mina.accesos.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "username es obligatorio")
        String username,
        @NotBlank(message = "password es obligatorio")
        String password
) {
}
