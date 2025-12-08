package com.mina.accesos.dto;

import com.mina.accesos.domain.UserAccount;

public record AuthResponse(
        String token,
        long expiresAt,
        String refreshToken,
        long refreshExpiresAt,
        String username,
        String role,
        String fullName,
        String qrCode
) {
    public static AuthResponse from(TokenPair tokens, UserAccount user) {
        return new AuthResponse(
                tokens.token(),
                tokens.expiresAt(),
                tokens.refreshToken(),
                tokens.refreshExpiresAt(),
                user.getUsername(),
                user.getRole().name(),
                user.getFullName(),
                user.getQrCode()
        );
    }
}
