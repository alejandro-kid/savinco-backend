package com.savinco.financial.infrastructure.persistence.repository;

import com.savinco.financial.infrastructure.persistence.entity.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataCountryRepository extends JpaRepository<CountryEntity, Long> {
    Optional<CountryEntity> findByCode(String code);
    boolean existsByCode(String code);
}

