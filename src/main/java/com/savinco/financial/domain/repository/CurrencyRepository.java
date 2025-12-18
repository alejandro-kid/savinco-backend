package com.savinco.financial.domain.repository;

import java.util.List;
import java.util.Optional;

import com.savinco.financial.domain.model.Currency;
import com.savinco.financial.domain.model.CurrencyCode;

public interface CurrencyRepository {
    Currency save(Currency currency);
    Optional<Currency> findById(Long id);
    Optional<Currency> findByCode(CurrencyCode code);
    List<Currency> findAll();
    Optional<Currency> findBaseCurrency();
    boolean existsByCode(CurrencyCode code);
    long count();
    void deleteById(Long id);
    void deleteAll();
}

