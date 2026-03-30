package com.example.Auth.UserService.repository;

import com.example.Auth.UserService.entity.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {
    public Optional<UserDetails> findByUserId(Long userId);
}
