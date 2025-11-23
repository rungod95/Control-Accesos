package com.mina.accesos.service;

import com.mina.accesos.domain.Role;
import com.mina.accesos.domain.UserAccount;
import com.mina.accesos.exception.NotFoundException;
import com.mina.accesos.repository.UserAccountRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserAccountService {

    private final UserAccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    public List<UserAccount> findAll() {
        return repository.findAll();
    }

    public UserAccount findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado: " + id));
    }

    public UserAccount findByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado: " + username));
    }

    public UserAccount findByQrCode(String qrCode) {
        return repository.findByQrCodeIgnoreCase(qrCode)
                .orElseThrow(() -> new NotFoundException("QR no asociado a ningún usuario: " + qrCode));
    }

    public UserAccount createUser(String username, String rawPassword, Role role, String fullName, String qrCode) {
        if (repository.existsByUsername(username)) {
            throw new IllegalArgumentException("Ya existe un usuario con username " + username);
        }
        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setFullName(fullName);
        user.setEnabled(true);
        user.setQrCode(qrCode);
        return repository.save(user);
    }

    public UserAccount updateUser(Long id, String fullName, Role role, String rawPassword, String qrCode) {
        UserAccount current = findById(id);
        current.setFullName(fullName);
        current.setRole(role);
        current.setQrCode(qrCode);
        if (StringUtils.hasText(rawPassword)) {
            current.setPassword(passwordEncoder.encode(rawPassword));
        }
        return repository.save(current);
    }

    public void deleteUser(Long id) {
        repository.deleteById(id);
    }
}
