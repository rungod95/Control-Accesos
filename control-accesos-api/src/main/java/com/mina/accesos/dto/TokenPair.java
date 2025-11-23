package com.mina.accesos.dto;

public record TokenPair(
        String token,
        long expiresAt,
        String refreshToken,
        long refreshExpiresAt
) {
}
