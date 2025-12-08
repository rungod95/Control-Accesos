package com.mina.accesos.service;

import com.mina.accesos.dto.TokenPair;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${jwt.refresh-expiration}")
    private long jwtRefreshExpirationMs;

    public TokenPair generateTokens(String username) {
        long now = System.currentTimeMillis();
        long accessExpiresAt = now + jwtExpirationMs;
        long refreshExpiresAt = now + jwtRefreshExpirationMs;
        String accessToken = buildToken(username, now, accessExpiresAt);
        String refreshToken = buildToken(username, now, refreshExpiresAt);
        return new TokenPair(accessToken, accessExpiresAt, refreshToken, refreshExpiresAt);
    }

    private String buildToken(String username, long issuedAt, long expiresAt) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(issuedAt))
                .setExpiration(new Date(expiresAt))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey.getBytes()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
