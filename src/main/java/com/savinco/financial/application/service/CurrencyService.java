package com.savinco.financial.application.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.savinco.financial.domain.model.Currency;
import com.savinco.financial.domain.model.CurrencyCode;
import com.savinco.financial.domain.model.CurrencyPrimitives;
import com.savinco.financial.domain.repository.CurrencyRepository;
import com.savinco.financial.domain.repository.CountryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyRepository repository;
    private final CountryRepository countryRepository;

    @Transactional
    public Currency create(String code, String name, Boolean isBase, BigDecimal exchangeRateToBase) {
        // Validate code format
        CurrencyCode currencyCode = new CurrencyCode(code);

        // Check if currency already exists
        if (repository.existsByCode(currencyCode)) {
            throw new IllegalStateException("Currency already exists with code: " + code);
        }

        // Determine if this will be the base currency
        boolean willBeBase;
        BigDecimal finalExchangeRate;
        
        long currencyCount = repository.count();
        
        if (currencyCount == 0) {
            // First currency: automatically becomes base with rate = 1
            willBeBase = true;
            finalExchangeRate = BigDecimal.ONE;
        } else {
            // Not the first currency: cannot be base
            if (isBase != null && isBase) {
                throw new IllegalStateException(
                    "Cannot create currency as base. Base currency already exists. " +
                    "Only the first currency created becomes the base currency automatically."
                );
            }
            willBeBase = false;
            finalExchangeRate = exchangeRateToBase;
        }

        // Create domain entity
        Currency currency = Currency.create(CurrencyPrimitives.builder()
            .code(code)
            .name(name)
            .isBase(willBeBase)
            .exchangeRateToBase(finalExchangeRate)
            .build());

        // Save
        return repository.save(currency);
    }

    public List<Currency> findAll() {
        return repository.findAll();
    }

    public Currency findByCode(String code) {
        CurrencyCode currencyCode = new CurrencyCode(code);
        return repository.findByCode(currencyCode)
            .orElseThrow(() -> new IllegalStateException("Currency not found with code: " + code));
    }

    public Currency getBaseCurrency() {
        return repository.findBaseCurrency()
            .orElseThrow(() -> new IllegalStateException("Base currency not found"));
    }

    @Transactional
    public Currency updateExchangeRate(String code, BigDecimal newRate) {
        CurrencyCode currencyCode = new CurrencyCode(code);
        Currency currency = repository.findByCode(currencyCode)
            .orElseThrow(() -> new IllegalStateException("Currency not found with code: " + code));

        // Update exchange rate (validates in domain)
        currency.updateExchangeRate(newRate);

        // Save
        return repository.save(currency);
    }

    @Transactional
    public void delete(String code) {
        CurrencyCode currencyCode = new CurrencyCode(code);
        Currency currency = repository.findByCode(currencyCode)
            .orElseThrow(() -> new IllegalStateException("Currency not found with code: " + code));

        // Validate that no countries are using this currency
        if (countryRepository.existsByCurrencyId(currency.getId().getValue())) {
            throw new IllegalStateException("Cannot delete currency with code: " + code + ". There are countries associated with this currency.");
        }

        // Validate base currency deletion: can only delete base currency if it's the only currency
        if (currency.isBase()) {
            long currencyCount = repository.count();
            if (currencyCount > 1) {
                throw new IllegalStateException(
                    "Cannot delete base currency with code: " + code + 
                    ". Base currency can only be deleted if it is the only currency in the database."
                );
            }
        }

        // Delete
        repository.deleteById(currency.getId().getValue());
    }
}

