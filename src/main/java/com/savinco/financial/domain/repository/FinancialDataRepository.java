package com.savinco.financial.domain.repository;

import java.util.List;
import java.util.Optional;

import com.savinco.financial.domain.model.Country;
import com.savinco.financial.domain.model.FinancialData;

public interface FinancialDataRepository {
    FinancialData save(FinancialData financialData);
    Optional<FinancialData> findByCountryCode(Country country);
    boolean existsByCountryCode(Country country);
    void deleteByCountryCode(Country country);
    List<FinancialData> findAll();
    void deleteAll();
}
