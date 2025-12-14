package com.savinco.financial.domain.repository;

import com.savinco.financial.domain.model.Country;
import com.savinco.financial.domain.model.FinancialData;

import java.util.Optional;

public interface FinancialDataRepository {
    FinancialData save(FinancialData financialData);
    Optional<FinancialData> findByCountryCode(Country country);
    boolean existsByCountryCode(Country country);
    void deleteByCountryCode(Country country);
}
