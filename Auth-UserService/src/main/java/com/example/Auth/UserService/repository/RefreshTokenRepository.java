package com.example.Auth.UserService.repository;

import com.example.Auth.UserService.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    public Optional<RefreshToken> findByToken(String token);
    public void deleteByUserId(Long userId);
}
