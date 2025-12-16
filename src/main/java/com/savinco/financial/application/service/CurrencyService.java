package com.savinco.financial.application.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.savinco.financial.domain.model.Currency;
import com.savinco.financial.domain.model.CurrencyCode;
import com.savinco.financial.domain.model.CurrencyPrimitives;
import com.savinco.financial.domain.repository.CurrencyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyRepository repository;

    @Transactional
    public Currency create(String code, String name, Boolean isBase, BigDecimal exchangeRateToBase) {
        // Validate code format
        CurrencyCode currencyCode = new CurrencyCode(code);

        // Check if currency already exists
        if (repository.existsByCode(currencyCode)) {
            throw new IllegalStateException("Currency already exists with code: " + code);
        }

        // Validate base currency constraint
        if (isBase) {
            repository.findBaseCurrency()
                .ifPresent(existingBase -> {
                    throw new IllegalStateException(
                        "Base currency already exists: " + existingBase.getCode().getValue()
                    );
                });
        }

        // Create domain entity
        Currency currency = Currency.create(CurrencyPrimitives.builder()
            .code(code)
            .name(name)
            .isBase(isBase)
            .exchangeRateToBase(exchangeRateToBase)
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
}

