package com.savinco.financial.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.savinco.financial.domain.model.Country;
import com.savinco.financial.domain.model.CountryCode;
import com.savinco.financial.domain.model.CountryPrimitives;
import com.savinco.financial.domain.model.Currency;
import com.savinco.financial.domain.model.CurrencyCode;
import com.savinco.financial.domain.repository.CountryRepository;
import com.savinco.financial.domain.repository.CurrencyRepository;
import com.savinco.financial.domain.repository.FinancialDataRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository repository;
    private final CurrencyRepository currencyRepository;
    private final FinancialDataRepository financialDataRepository;

    @Transactional
    public Country create(String code, String name, String currencyCode) {
        // Validate country code format
        CountryCode countryCode = new CountryCode(code);

        // Check if country already exists
        if (repository.existsByCode(countryCode)) {
            throw new IllegalStateException("Country already exists with code: " + code);
        }

        // Validate currency exists
        CurrencyCode currencyCodeObj = new CurrencyCode(currencyCode);
        Currency currency = currencyRepository.findByCode(currencyCodeObj)
            .orElseThrow(() -> new IllegalStateException("Currency not found with code: " + currencyCode));

        // Create domain entity
        Country country = Country.create(CountryPrimitives.builder()
            .code(code)
            .name(name)
            .currencyId(currency.getId().getValue())
            .build());

        // Save
        return repository.save(country);
    }

    public List<Country> findAll() {
        return repository.findAll();
    }

    public Country findByCode(String code) {
        CountryCode countryCode = new CountryCode(code);
        return repository.findByCode(countryCode)
            .orElseThrow(() -> new IllegalStateException("Country not found with code: " + code));
    }

    @Transactional
    public void delete(String code) {
        CountryCode countryCode = new CountryCode(code);
        Country country = repository.findByCode(countryCode)
            .orElseThrow(() -> new IllegalStateException("Country not found with code: " + code));

        // Validate that no financial data is associated with this country
        if (financialDataRepository.existsByCountryId(country.getId().getValue())) {
            throw new IllegalStateException("Cannot delete country with code: " + code + ". There are financial data associated with this country.");
        }

        // Delete
        repository.deleteById(country.getId().getValue());
    }
}

