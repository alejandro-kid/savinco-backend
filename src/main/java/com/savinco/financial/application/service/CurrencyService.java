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
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {

    private final CurrencyRepository repository;
    private final CountryRepository countryRepository;

    @Transactional
    public Currency create(String code, String name, Boolean isBase, BigDecimal exchangeRateToBase) {
        log.debug("Creating currency: code={}, name={}, isBase={}, exchangeRateToBase={}", 
            code, name, isBase, exchangeRateToBase);
        
        // Validate code format
        CurrencyCode currencyCode = new CurrencyCode(code);

        // Check if currency already exists
        if (repository.existsByCode(currencyCode)) {
            log.warn("Currency creation failed: currency already exists with code={}", code);
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
            log.info("First currency created, automatically set as base: code={}", code);
        } else {
            // Not the first currency: cannot be base
            if (isBase != null && isBase) {
                log.warn("Currency creation failed: attempt to create base currency when one already exists");
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
        Currency saved = repository.save(currency);
        log.info("Currency created successfully: code={}, id={}, isBase={}", 
            code, saved.getId().getValue(), saved.isBase());
        return saved;
    }

    public List<Currency> findAll() {
        log.debug("Finding all currencies");
        List<Currency> currencies = repository.findAll();
        log.debug("Found {} currencies", currencies.size());
        return currencies;
    }

    public Currency findByCode(String code) {
        CurrencyCode currencyCode = new CurrencyCode(code);
        return repository.findByCode(currencyCode)
            .orElseThrow(() -> new IllegalStateException("Currency not found with code: " + code));
    }

    public Currency getBaseCurrency() {
        log.debug("Finding base currency");
        Currency currency = repository.findBaseCurrency()
            .orElseThrow(() -> {
                log.warn("Base currency not found");
                return new IllegalStateException("Base currency not found");
            });
        log.debug("Base currency found: code={}, id={}", currency.getCode().getValue(), currency.getId().getValue());
        return currency;
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
        log.debug("Deleting currency: code={}", code);
        CurrencyCode currencyCode = new CurrencyCode(code);
        Currency currency = repository.findByCode(currencyCode)
            .orElseThrow(() -> {
                log.warn("Currency deletion failed: currency not found with code={}", code);
                return new IllegalStateException("Currency not found with code: " + code);
            });

        // Validate that no countries are using this currency
        if (countryRepository.existsByCurrencyId(currency.getId().getValue())) {
            log.warn("Currency deletion failed: countries associated with currency code={}", code);
            throw new IllegalStateException("Cannot delete currency with code: " + code + ". There are countries associated with this currency.");
        }

        // Validate base currency deletion: can only delete base currency if it's the only currency
        if (currency.isBase()) {
            long currencyCount = repository.count();
            if (currencyCount > 1) {
                log.warn("Currency deletion failed: cannot delete base currency when other currencies exist, code={}", code);
                throw new IllegalStateException(
                    "Cannot delete base currency with code: " + code + 
                    ". Base currency can only be deleted if it is the only currency in the database."
                );
            }
        }

        // Delete
        repository.deleteById(currency.getId().getValue());
        log.info("Currency deleted successfully: code={}, id={}", code, currency.getId().getValue());
    }
}

