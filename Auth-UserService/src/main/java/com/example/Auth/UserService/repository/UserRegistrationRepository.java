package com.example.Auth.UserService.repository;

import com.example.Auth.UserService.entity.UserRegistration;
import com.example.Auth.UserService.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRegistrationRepository extends JpaRepository<UserRegistration, Long> {
    public Optional<UserRegistration> findByEmail(String email);
    public boolean existsByEmail(String email);
    public boolean existsByRole(Role role);
}
