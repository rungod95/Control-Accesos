package com.mina.accesos.dto;

import jakarta.validation.constraints.NotBlank;

public record UserAccountUpdateRequest(
        @NotBlank(message = "role es obligatorio")
        String role,
        String fullName,
        String password
) {
}
