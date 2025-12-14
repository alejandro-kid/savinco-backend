package com.savinco.financial.infrastructure.persistence.repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.savinco.financial.domain.model.Country;
import com.savinco.financial.domain.model.Currency;
import com.savinco.financial.domain.model.FinancialData;
import com.savinco.financial.domain.repository.FinancialDataRepository;
import com.savinco.financial.infrastructure.persistence.entity.FinancialDataEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JpaFinancialDataRepository implements FinancialDataRepository {

    private final SpringDataFinancialDataRepository springDataRepository;

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
    public Optional<FinancialData> findByCountryCode(Country country) {
        return springDataRepository.findByCountryCode(country.getCode())
            .map(this::toDomain);
    }

    @Override
    public boolean existsByCountryCode(Country country) {
        return springDataRepository.existsByCountryCode(country.getCode());
    }

    @Override
    public void deleteByCountryCode(Country country) {
        springDataRepository.deleteByCountryCode(country.getCode());
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
            .countryCode(domain.getCountry().getCode())
            .currencyCode(domain.getCurrency().getCode())
            .capitalSaved(domain.getCapitalSaved())
            .capitalLoaned(domain.getCapitalLoaned())
            .profitsGenerated(domain.getProfitsGenerated())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }

    private FinancialData toDomain(@NonNull FinancialDataEntity entity) {
        Country country = Country.fromCode(entity.getCountryCode());
        Currency currency = Currency.fromCode(entity.getCurrencyCode());
        
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
