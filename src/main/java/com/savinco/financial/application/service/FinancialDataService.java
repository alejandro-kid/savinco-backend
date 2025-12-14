package com.savinco.financial.application.service;

import com.savinco.financial.application.dto.FinancialDataRequest;
import com.savinco.financial.application.dto.FinancialDataResponse;
import com.savinco.financial.domain.model.Country;
import com.savinco.financial.domain.model.Currency;
import com.savinco.financial.domain.model.FinancialData;
import com.savinco.financial.domain.repository.FinancialDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class FinancialDataService {

    private final FinancialDataRepository repository;
    private final CurrencyConverterService currencyConverter;

    @Transactional
    public FinancialDataResponse create(FinancialDataRequest request) {
        // Validate country code
        Country country = Country.fromCode(request.getCountryCode());
        if (country == null) {
            throw new IllegalArgumentException("Invalid country code: " + request.getCountryCode());
        }

        // Validate currency code
        Currency currency = Currency.fromCode(request.getCurrencyCode());
        if (currency == null) {
            throw new IllegalArgumentException("Invalid currency code: " + request.getCurrencyCode());
        }

        // Validate currency matches country (before checking existence)
        if (!country.isValidCurrency(currency)) {
            throw new IllegalArgumentException(
                "Currency " + currency.getCode() + " does not match country " + country.getCode() + 
                ". Expected currency: " + country.getCurrency().getCode()
            );
        }

        // Check if country already exists
        if (repository.existsByCountryCode(country)) {
            throw new IllegalStateException("Financial data already exists for country: " + request.getCountryCode());
        }

        // Create domain entity
        FinancialData financialData = new FinancialData(
            country,
            currency,
            request.getCapitalSaved(),
            request.getCapitalLoaned(),
            request.getProfitsGenerated()
        );

        // Validate domain rules
        financialData.validate();

        // Save
        FinancialData saved = repository.save(financialData);

        // Convert to USD and build response
        return buildResponse(saved);
    }

    private FinancialDataResponse buildResponse(FinancialData financialData) {
        BigDecimal capitalSavedUSD = currencyConverter.convertToUSD(
            financialData.getCurrency(), 
            financialData.getCapitalSaved()
        );
        BigDecimal capitalLoanedUSD = currencyConverter.convertToUSD(
            financialData.getCurrency(), 
            financialData.getCapitalLoaned()
        );
        BigDecimal profitsGeneratedUSD = currencyConverter.convertToUSD(
            financialData.getCurrency(), 
            financialData.getProfitsGenerated()
        );
        BigDecimal totalUSD = capitalSavedUSD.add(capitalLoanedUSD).add(profitsGeneratedUSD);

        return FinancialDataResponse.builder()
            .countryCode(financialData.getCountry().getCode())
            .countryName(financialData.getCountry().getName())
            .originalCurrency(financialData.getCurrency().getCode())
            .capitalSaved(capitalSavedUSD)
            .capitalLoaned(capitalLoanedUSD)
            .profitsGenerated(profitsGeneratedUSD)
            .totalInUSD(totalUSD)
            .build();
    }
}
