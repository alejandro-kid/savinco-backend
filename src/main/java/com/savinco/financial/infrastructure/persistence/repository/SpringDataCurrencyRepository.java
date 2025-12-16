package com.savinco.financial.infrastructure.persistence.repository;

import com.savinco.financial.infrastructure.persistence.entity.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataCurrencyRepository extends JpaRepository<CurrencyEntity, Long> {
    Optional<CurrencyEntity> findByCode(String code);
    boolean existsByCode(String code);
    Optional<CurrencyEntity> findByIsBaseTrue();
}

