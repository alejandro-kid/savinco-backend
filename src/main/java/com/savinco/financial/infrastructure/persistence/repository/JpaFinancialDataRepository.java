package com.savinco.financial.infrastructure.persistence.repository;

import com.savinco.financial.domain.model.Country;
import com.savinco.financial.domain.model.Currency;
import com.savinco.financial.domain.model.FinancialData;
import com.savinco.financial.domain.repository.FinancialDataRepository;
import com.savinco.financial.infrastructure.persistence.entity.FinancialDataEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaFinancialDataRepository implements FinancialDataRepository {

    private final SpringDataFinancialDataRepository springDataRepository;

    @Override
    public FinancialData save(FinancialData financialData) {
        FinancialDataEntity entity = toEntity(financialData);
        FinancialDataEntity saved = springDataRepository.save(entity);
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

    private FinancialData toDomain(FinancialDataEntity entity) {
        Country country = Country.fromCode(entity.getCountryCode());
        Currency currency = Currency.fromCode(entity.getCurrencyCode());
        
        return new FinancialData(
            entity.getId(),
            country,
            currency,
            entity.getCapitalSaved(),
            entity.getCapitalLoaned(),
            entity.getProfitsGenerated(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
