package com.mina.accesos.controller;

import com.mina.accesos.dto.AuthResponse;
import com.mina.accesos.dto.LoginRequest;
import com.mina.accesos.dto.RefreshTokenRequest;
import com.mina.accesos.dto.TokenPair;
import com.mina.accesos.service.UserAccountService;
import com.mina.accesos.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserAccountService userAccountService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            String username = authentication.getName();
            TokenPair tokens = jwtService.generateTokens(username);
            AuthResponse response = AuthResponse.from(tokens, userAccountService.findByUsername(username));
            return ResponseEntity.ok(response);
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        if (!jwtService.isTokenValid(request.refreshToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = jwtService.extractUsername(request.refreshToken());
        TokenPair tokens = jwtService.generateTokens(username);
        AuthResponse response = AuthResponse.from(tokens, userAccountService.findByUsername(username));
        return ResponseEntity.ok(response);
    }
}
