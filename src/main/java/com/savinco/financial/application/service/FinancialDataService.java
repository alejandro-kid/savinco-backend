package com.savinco.financial.application.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.savinco.financial.application.dto.ConsolidatedSummaryResponse;
import com.savinco.financial.application.dto.FinancialDataRequest;
import com.savinco.financial.application.dto.FinancialDataResponse;
import com.savinco.financial.domain.model.Country;
import com.savinco.financial.domain.model.CountryCode;
import com.savinco.financial.domain.model.Currency;
import com.savinco.financial.domain.model.CurrencyCode;
import com.savinco.financial.domain.model.FinancialData;
import com.savinco.financial.domain.repository.CountryRepository;
import com.savinco.financial.domain.repository.CurrencyRepository;
import com.savinco.financial.domain.repository.FinancialDataRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialDataService {

    private final FinancialDataRepository repository;
    private final CountryRepository countryRepository;
    private final CurrencyRepository currencyRepository;
    private final CurrencyConverterService currencyConverter;

    @Transactional
    public FinancialDataResponse create(FinancialDataRequest request) {
        log.debug("Creating financial data: countryCode={}, currencyCode={}, capitalSaved={}, capitalLoaned={}, profitsGenerated={}", 
            request.getCountryCode(), request.getCurrencyCode(), 
            request.getCapitalSaved(), request.getCapitalLoaned(), request.getProfitsGenerated());
        
        // Validate country code
        CountryCode countryCode = new CountryCode(request.getCountryCode());
        Country country = countryRepository.findByCode(countryCode)
            .orElseThrow(() -> {
                log.warn("Financial data creation failed: country not found with code={}", request.getCountryCode());
                return new IllegalStateException("Country not found with code: " + request.getCountryCode());
            });

        // Validate currency code
        CurrencyCode currencyCode = new CurrencyCode(request.getCurrencyCode());
        Currency currency = currencyRepository.findByCode(currencyCode)
            .orElseThrow(() -> {
                log.warn("Financial data creation failed: currency not found with code={}", request.getCurrencyCode());
                return new IllegalStateException("Currency not found with code: " + request.getCurrencyCode());
            });

        // Validate currency matches country (before checking existence)
        if (!country.isValidCurrency(currency)) {
            log.warn("Financial data creation failed: currency {} does not match country {}", 
                currency.getCode().getValue(), country.getCode().getValue());
            throw new IllegalArgumentException(
                "Currency " + currency.getCode().getValue() + " does not match country " + country.getCode().getValue()
            );
        }

        // Check if country already exists
        if (repository.existsByCountryCode(countryCode)) {
            log.warn("Financial data creation failed: financial data already exists for country={}", request.getCountryCode());
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
        log.info("Financial data created successfully: countryCode={}", request.getCountryCode());

        // Convert to USD and build response
        return buildResponse(saved);
    }

    public List<FinancialDataResponse> findAll() {
        log.debug("Finding all financial data");
        List<FinancialDataResponse> response = repository.findAll().stream()
            .map(this::buildResponse)
            .toList();
        log.debug("Found {} financial data records", response.size());
        return response;
    }

    public FinancialDataResponse findByCountryCode(String countryCode) {
        log.debug("Finding financial data by country code: {}", countryCode);
        CountryCode code = new CountryCode(countryCode);
        Country country = countryRepository.findByCode(code)
            .orElseThrow(() -> {
                log.warn("Financial data lookup failed: country not found with code={}", countryCode);
                return new IllegalStateException("Country not found with code: " + countryCode);
            });

        FinancialData financialData = repository.findByCountryCode(code)
            .orElseThrow(() -> {
                log.warn("Financial data not found for country: code={}", countryCode);
                return new IllegalStateException("Financial data not found for country: " + countryCode);
            });

        log.debug("Financial data found: countryCode={}", countryCode);
        return buildResponse(financialData);
    }

    @Transactional
    public FinancialDataResponse update(String countryCode, FinancialDataRequest request) {
        log.debug("Updating financial data: countryCode={}, capitalSaved={}, capitalLoaned={}, profitsGenerated={}", 
            countryCode, request.getCapitalSaved(), request.getCapitalLoaned(), request.getProfitsGenerated());
        
        // Validate country code from path
        CountryCode code = new CountryCode(countryCode);
        Country country = countryRepository.findByCode(code)
            .orElseThrow(() -> {
                log.warn("Financial data update failed: country not found with code={}", countryCode);
                return new IllegalStateException("Country not found with code: " + countryCode);
            });

        // Validate that country code in path matches body
        if (!countryCode.equals(request.getCountryCode())) {
            log.warn("Financial data update failed: country code mismatch. path={}, body={}", 
                countryCode, request.getCountryCode());
            throw new IllegalArgumentException(
                "Country code in path (" + countryCode + ") does not match country code in body (" + request.getCountryCode() + ")"
            );
        }

        // Validate currency code
        CurrencyCode currencyCode = new CurrencyCode(request.getCurrencyCode());
        Currency currency = currencyRepository.findByCode(currencyCode)
            .orElseThrow(() -> {
                log.warn("Financial data update failed: currency not found with code={}", request.getCurrencyCode());
                return new IllegalStateException("Currency not found with code: " + request.getCurrencyCode());
            });

        // Validate currency matches country
        if (!country.isValidCurrency(currency)) {
            log.warn("Financial data update failed: currency {} does not match country {}", 
                currency.getCode().getValue(), country.getCode().getValue());
            throw new IllegalArgumentException(
                "Currency " + currency.getCode().getValue() + " does not match country " + country.getCode().getValue()
            );
        }

        // Check if country exists
        FinancialData existingData = repository.findByCountryCode(code)
            .orElseThrow(() -> {
                log.warn("Financial data update failed: financial data not found for country={}", countryCode);
                return new IllegalStateException("Financial data not found for country: " + countryCode);
            });

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
        log.info("Financial data updated successfully: countryCode={}", countryCode);

        // Convert to USD and build response
        return buildResponse(saved);
    }

    /**
     * Generates a consolidated summary of all financial data converted to USD.
     * 
     * Conversion uses: amountInUSD = amountInOriginalCurrency / exchangeRateToBase
     * where exchangeRateToBase represents: 1 USD = X units of original currency
     */
    public ConsolidatedSummaryResponse getSummary() {
        log.debug("Generating consolidated summary");
        List<FinancialData> allData = repository.findAll();

        List<ConsolidatedSummaryResponse.CountrySummary> countrySummaries = allData.stream()
            .map(data -> {
                // Convert each amount using: amountInUSD = amountInOriginalCurrency / exchangeRateToBase
                // where exchangeRateToBase represents: 1 USD = X units of original currency
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
                    .countryCode(data.getCountry().getCode().getValue())
                    .countryName(data.getCountry().getName().getValue())
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
        log.debug("Deleting financial data: countryCode={}", countryCode);
        
        // Validate country code
        CountryCode code = new CountryCode(countryCode);
        countryRepository.findByCode(code)
            .orElseThrow(() -> {
                log.warn("Financial data deletion failed: country not found with code={}", countryCode);
                return new IllegalStateException("Country not found with code: " + countryCode);
            });

        // Check if country exists
        if (!repository.existsByCountryCode(code)) {
            log.warn("Financial data deletion failed: financial data not found for country={}", countryCode);
            throw new IllegalStateException("Financial data not found for country: " + countryCode);
        }

        // Delete
        repository.deleteByCountryCode(code);
        log.info("Financial data deleted successfully: countryCode={}", countryCode);
    }

    private FinancialDataResponse buildResponse(FinancialData financialData) {
        // Return values in original currency (not converted to USD)
        // Conversion to USD only happens in getSummary()
        BigDecimal capitalSaved = financialData.getCapitalSaved();
        BigDecimal capitalLoaned = financialData.getCapitalLoaned();
        BigDecimal profitsGenerated = financialData.getProfitsGenerated();
        BigDecimal total = capitalSaved.add(capitalLoaned).add(profitsGenerated);

        return FinancialDataResponse.builder()
            .countryCode(financialData.getCountry().getCode().getValue())
            .countryName(financialData.getCountry().getName().getValue())
            .originalCurrency(financialData.getCurrency().getCode().getValue())
            .capitalSaved(capitalSaved)
            .capitalLoaned(capitalLoaned)
            .profitsGenerated(profitsGenerated)
            .totalInUSD(total)
            .build();
    }
}
