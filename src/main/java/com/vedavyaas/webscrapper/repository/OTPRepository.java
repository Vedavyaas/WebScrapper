package com.vedavyaas.webscrapper.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OTPRepository extends JpaRepository<OTPEntity, Long> {
    OTPEntity findOTPEntityByEmail(String email);
    boolean existsByEmail(String email);
    void deleteByEmail(String email);
}
