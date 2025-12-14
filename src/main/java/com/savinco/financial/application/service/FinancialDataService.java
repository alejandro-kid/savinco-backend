package com.savinco.financial.application.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.savinco.financial.application.dto.ConsolidatedSummaryResponse;
import com.savinco.financial.application.dto.FinancialDataRequest;
import com.savinco.financial.application.dto.FinancialDataResponse;
import com.savinco.financial.domain.model.Country;
import com.savinco.financial.domain.model.Currency;
import com.savinco.financial.domain.model.FinancialData;
import com.savinco.financial.domain.repository.FinancialDataRepository;

import lombok.RequiredArgsConstructor;

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
                "Currency " + currency.getCode() + " does not match country " + country.getCode()
                + ". Expected currency: " + country.getCurrency().getCode()
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

    public List<FinancialDataResponse> findAll() {
        return repository.findAll().stream()
            .map(this::buildResponse)
            .toList();
    }

    public FinancialDataResponse findByCountryCode(String countryCode) {
        Country country = Country.fromCode(countryCode);
        if (country == null) {
            throw new IllegalArgumentException("Invalid country code: " + countryCode);
        }

        FinancialData financialData = repository.findByCountryCode(country)
            .orElseThrow(() -> new IllegalStateException("Financial data not found for country: " + countryCode));

        return buildResponse(financialData);
    }

    @Transactional
    public FinancialDataResponse update(String countryCode, FinancialDataRequest request) {
        // Validate country code from path
        Country country = Country.fromCode(countryCode);
        if (country == null) {
            throw new IllegalArgumentException("Invalid country code: " + countryCode);
        }

        // Validate that country code in path matches body
        if (!countryCode.equals(request.getCountryCode())) {
            throw new IllegalArgumentException(
                "Country code in path (" + countryCode + ") does not match country code in body (" + request.getCountryCode() + ")"
            );
        }

        // Validate currency code
        Currency currency = Currency.fromCode(request.getCurrencyCode());
        if (currency == null) {
            throw new IllegalArgumentException("Invalid currency code: " + request.getCurrencyCode());
        }

        // Validate currency matches country
        if (!country.isValidCurrency(currency)) {
            throw new IllegalArgumentException(
                "Currency " + currency.getCode() + " does not match country " + country.getCode()
                + ". Expected currency: " + country.getCurrency().getCode()
            );
        }

        // Check if country exists
        FinancialData existingData = repository.findByCountryCode(country)
            .orElseThrow(() -> new IllegalStateException("Financial data not found for country: " + countryCode));

        // Create updated domain entity (preserving id and created timestamp)
        // updatedAt will be set automatically by JPA @PreUpdate
        FinancialData updatedData = new FinancialData(
            existingData.getId(),
            country,
            currency,
            request.getCapitalSaved(),
            request.getCapitalLoaned(),
            request.getProfitsGenerated(),
            new FinancialData.Timestamps(existingData.getCreatedAt(), existingData.getUpdatedAt())
        );

        // Validate domain rules
        updatedData.validate();

        // Save
        FinancialData saved = repository.save(updatedData);

        // Convert to USD and build response
        return buildResponse(saved);
    }

    public ConsolidatedSummaryResponse getSummary() {
        List<FinancialData> allData = repository.findAll();

        List<ConsolidatedSummaryResponse.CountrySummary> countrySummaries = allData.stream()
            .map(data -> {
                BigDecimal capitalSavedUSD = currencyConverter.convertToUSD(
                    data.getCurrency(), 
                    data.getCapitalSaved()
                );
                BigDecimal capitalLoanedUSD = currencyConverter.convertToUSD(
                    data.getCurrency(), 
                    data.getCapitalLoaned()
                );
                BigDecimal profitsGeneratedUSD = currencyConverter.convertToUSD(
                    data.getCurrency(), 
                    data.getProfitsGenerated()
                );

                return ConsolidatedSummaryResponse.CountrySummary.builder()
                    .countryCode(data.getCountry().getCode())
                    .countryName(data.getCountry().getName())
                    .capitalSaved(capitalSavedUSD)
                    .capitalLoaned(capitalLoanedUSD)
                    .profitsGenerated(profitsGeneratedUSD)
                    .build();
            })
            .toList();

        // Calculate totals from country summaries
        BigDecimal totalCapitalSaved = countrySummaries.stream()
            .map(ConsolidatedSummaryResponse.CountrySummary::getCapitalSaved)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalCapitalLoaned = countrySummaries.stream()
            .map(ConsolidatedSummaryResponse.CountrySummary::getCapitalLoaned)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalProfitsGenerated = countrySummaries.stream()
            .map(ConsolidatedSummaryResponse.CountrySummary::getProfitsGenerated)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal grandTotal = totalCapitalSaved
            .add(totalCapitalLoaned)
            .add(totalProfitsGenerated);

        return ConsolidatedSummaryResponse.builder()
            .totalCapitalSaved(totalCapitalSaved)
            .totalCapitalLoaned(totalCapitalLoaned)
            .totalProfitsGenerated(totalProfitsGenerated)
            .grandTotal(grandTotal)
            .byCountry(countrySummaries)
            .build();
    }

    @Transactional
    public void delete(String countryCode) {
        // Validate country code
        Country country = Country.fromCode(countryCode);
        if (country == null) {
            throw new IllegalArgumentException("Invalid country code: " + countryCode);
        }

        // Check if country exists
        if (!repository.existsByCountryCode(country)) {
            throw new IllegalStateException("Financial data not found for country: " + countryCode);
        }

        // Delete
        repository.deleteByCountryCode(country);
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
