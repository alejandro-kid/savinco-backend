package com.savinco.financial.domain.repository;

import java.util.List;
import java.util.Optional;

import com.savinco.financial.domain.model.Country;
import com.savinco.financial.domain.model.CountryCode;

public interface CountryRepository {
    Country save(Country country);
    Optional<Country> findById(Long id);
    Optional<Country> findByCode(CountryCode code);
    List<Country> findAll();
    boolean existsByCode(CountryCode code);
    void deleteById(Long id);
    void deleteAll();
}

