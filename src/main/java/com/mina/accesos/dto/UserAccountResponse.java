package com.mina.accesos.dto;

public record UserAccountResponse(
        Long id,
        String username,
        String fullName,
        String role,
        boolean enabled
) {
}
