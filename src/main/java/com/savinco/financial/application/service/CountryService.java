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
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CountryService {

    private final CountryRepository repository;
    private final CurrencyRepository currencyRepository;
    private final FinancialDataRepository financialDataRepository;

    @Transactional
    public Country create(String code, String name, String currencyCode) {
        log.debug("Creating country: code={}, name={}, currencyCode={}", code, name, currencyCode);
        
        // Validate country code format
        CountryCode countryCode = new CountryCode(code);

        // Check if country already exists
        if (repository.existsByCode(countryCode)) {
            log.warn("Country creation failed: country already exists with code={}", code);
            throw new IllegalStateException("Country already exists with code: " + code);
        }

        // Validate currency exists
        CurrencyCode currencyCodeObj = new CurrencyCode(currencyCode);
        Currency currency = currencyRepository.findByCode(currencyCodeObj)
            .orElseThrow(() -> {
                log.warn("Country creation failed: currency not found with code={}", currencyCode);
                return new IllegalStateException("Currency not found with code: " + currencyCode);
            });

        // Create domain entity
        Country country = Country.create(CountryPrimitives.builder()
            .code(code)
            .name(name)
            .currencyId(currency.getId().getValue())
            .build());

        // Save
        Country saved = repository.save(country);
        log.info("Country created successfully: code={}, id={}", code, saved.getId().getValue());
        return saved;
    }

    public List<Country> findAll() {
        return repository.findAll();
    }

    public Country findByCode(String code) {
        log.debug("Finding country by code: {}", code);
        CountryCode countryCode = new CountryCode(code);
        Country country = repository.findByCode(countryCode)
            .orElseThrow(() -> {
                log.warn("Country not found: code={}", code);
                return new IllegalStateException("Country not found with code: " + code);
            });
        log.debug("Country found: code={}, id={}", code, country.getId().getValue());
        return country;
    }

    @Transactional
    public void delete(String code) {
        log.debug("Deleting country: code={}", code);
        CountryCode countryCode = new CountryCode(code);
        Country country = repository.findByCode(countryCode)
            .orElseThrow(() -> {
                log.warn("Country deletion failed: country not found with code={}", code);
                return new IllegalStateException("Country not found with code: " + code);
            });

        // Validate that no financial data is associated with this country
        if (financialDataRepository.existsByCountryId(country.getId().getValue())) {
            log.warn("Country deletion failed: financial data exists for country code={}", code);
            throw new IllegalStateException("Cannot delete country with code: " + code + ". There are financial data associated with this country.");
        }

        // Delete
        repository.deleteById(country.getId().getValue());
        log.info("Country deleted successfully: code={}, id={}", code, country.getId().getValue());
    }
}

