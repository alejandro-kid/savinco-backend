package com.savinco.financial.infrastructure.persistence.repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.savinco.financial.domain.model.Country;
import com.savinco.financial.domain.model.CountryCode;
import com.savinco.financial.domain.model.Currency;
import com.savinco.financial.domain.model.CurrencyCode;
import com.savinco.financial.domain.model.FinancialData;
import com.savinco.financial.domain.repository.CountryRepository;
import com.savinco.financial.domain.repository.CurrencyRepository;
import com.savinco.financial.domain.repository.FinancialDataRepository;
import com.savinco.financial.infrastructure.persistence.entity.FinancialDataEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JpaFinancialDataRepository implements FinancialDataRepository {

    private final SpringDataFinancialDataRepository springDataRepository;
    private final CountryRepository countryRepository;
    private final CurrencyRepository currencyRepository;

    @Override
    @SuppressWarnings("null")
    public FinancialData save(FinancialData financialData) {
        FinancialDataEntity entity = toEntity(financialData);
        FinancialDataEntity saved = Objects.requireNonNull(
            springDataRepository.save(entity),
            "Failed to save financial data entity"
        );
        return toDomain(saved);
    }

    @Override
    public Optional<FinancialData> findByCountryCode(CountryCode countryCode) {
        return springDataRepository.findByCountryCode(countryCode.getValue())
            .map(this::toDomain);
    }

    @Override
    public boolean existsByCountryCode(CountryCode countryCode) {
        return springDataRepository.existsByCountryCode(countryCode.getValue());
    }

    @Override
    public boolean existsByCountryId(Long countryId) {
        return springDataRepository.existsByCountryId(countryId);
    }

    @Override
    public void deleteByCountryCode(CountryCode countryCode) {
        springDataRepository.deleteByCountryCode(countryCode.getValue());
    }

    @Override
    public List<FinancialData> findAll() {
        return springDataRepository.findAll().stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public void deleteAll() {
        springDataRepository.deleteAll();
    }

    private FinancialDataEntity toEntity(FinancialData domain) {
        return FinancialDataEntity.builder()
            .id(domain.getId())
            .countryCode(domain.getCountry().getCode().getValue())
            .countryId(domain.getCountry().getId().getValue())
            .currencyCode(domain.getCurrency().getCode().getValue())
            .currencyId(domain.getCurrency().getId().getValue())
            .capitalSaved(domain.getCapitalSaved())
            .capitalLoaned(domain.getCapitalLoaned())
            .profitsGenerated(domain.getProfitsGenerated())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }

    private FinancialData toDomain(@NonNull FinancialDataEntity entity) {
        // Load Country and Currency entities from repositories using codes
        CountryCode countryCode = new CountryCode(entity.getCountryCode());
        Country country = countryRepository.findByCode(countryCode)
            .orElseThrow(() -> new IllegalStateException("Country not found with code: " + entity.getCountryCode()));
        
        CurrencyCode currencyCode = new CurrencyCode(entity.getCurrencyCode());
        Currency currency = currencyRepository.findByCode(currencyCode)
            .orElseThrow(() -> new IllegalStateException("Currency not found with code: " + entity.getCurrencyCode()));
        
        return new FinancialData(
            entity.getId(),
            country,
            currency,
            entity.getCapitalSaved(),
            entity.getCapitalLoaned(),
            entity.getProfitsGenerated(),
            new FinancialData.Timestamps(entity.getCreatedAt(), entity.getUpdatedAt())
        );
    }
}
