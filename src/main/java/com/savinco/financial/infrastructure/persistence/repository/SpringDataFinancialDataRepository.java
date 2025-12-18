package com.savinco.financial.infrastructure.persistence.repository;

import com.savinco.financial.infrastructure.persistence.entity.FinancialDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataFinancialDataRepository extends JpaRepository<FinancialDataEntity, Long> {
    Optional<FinancialDataEntity> findByCountryCode(String countryCode);
    boolean existsByCountryCode(String countryCode);
    boolean existsByCountryId(Long countryId);
    void deleteByCountryCode(String countryCode);
}
