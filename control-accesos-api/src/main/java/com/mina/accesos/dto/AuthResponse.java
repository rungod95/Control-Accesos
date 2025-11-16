package com.mina.accesos.dto;

public record AuthResponse(
        String token,
        long expiresAt,
        String refreshToken,
        long refreshExpiresAt
) {
}
