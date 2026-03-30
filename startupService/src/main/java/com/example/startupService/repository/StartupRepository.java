package com.example.startupService.repository;

import com.example.startupService.entity.Startup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StartupRepository extends JpaRepository<Startup, Long> {
    public List<Startup> findByFounderId(Long founderId);
    public Page<Startup> findAll(Pageable pageable);
}
