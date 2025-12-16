package com.savinco.financial.domain.repository;

import java.util.List;
import java.util.Optional;

import com.savinco.financial.domain.model.CountryCode;
import com.savinco.financial.domain.model.FinancialData;

public interface FinancialDataRepository {
    FinancialData save(FinancialData financialData);
    Optional<FinancialData> findByCountryCode(CountryCode countryCode);
    boolean existsByCountryCode(CountryCode countryCode);
    void deleteByCountryCode(CountryCode countryCode);
    List<FinancialData> findAll();
    void deleteAll();
}
