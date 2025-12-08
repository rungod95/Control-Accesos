package com.mina.accesos.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordChangeRequest(
        @NotBlank(message = "currentPassword es obligatorio")
        String currentPassword,
        @NotBlank(message = "newPassword es obligatorio")
        String newPassword
) {
}
