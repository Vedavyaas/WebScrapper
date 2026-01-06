package com.vedavyaas.webscrapper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface ProductScrapRepository extends JpaRepository<ProductScrapEntity, Long> {
    boolean existsByEmailAndUrl(String email, String url);

    boolean existsByEmail(String email);
    List<ProductScrapEntity> findAllByEmail(String email);

    @Modifying
    @Query("UPDATE ProductScrapEntity p SET p.url = :url WHERE p.id = :id")
    void updateUrlById(String url, Long id);

    @Modifying
    @Query("UPDATE ProductScrapEntity p SET p.targetPrice = :targetPrice WHERE p.id = :id")
    void  updateTargetPriceById(BigDecimal targetPrice, Long id);
}
