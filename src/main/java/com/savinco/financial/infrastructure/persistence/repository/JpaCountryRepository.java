package com.savinco.financial.infrastructure.persistence.repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.savinco.financial.domain.model.Country;
import com.savinco.financial.domain.model.CountryCode;
import com.savinco.financial.domain.model.CountryId;
import com.savinco.financial.domain.model.CountryName;
import com.savinco.financial.domain.model.CountryPrimitives;
import com.savinco.financial.domain.model.CurrencyId;
import com.savinco.financial.domain.repository.CountryRepository;
import com.savinco.financial.infrastructure.persistence.entity.CountryEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JpaCountryRepository implements CountryRepository {

    private final SpringDataCountryRepository springDataRepository;

    @Override
    @SuppressWarnings("null")
    public Country save(Country country) {
        CountryEntity entity = toEntity(country);
        CountryEntity saved = Objects.requireNonNull(
            springDataRepository.save(entity),
            "Failed to save country entity"
        );
        return toDomain(saved);
    }

    @Override
    public Optional<Country> findById(Long id) {
        return springDataRepository.findById(id)
            .map(this::toDomain);
    }

    @Override
    public Optional<Country> findByCode(CountryCode code) {
        return springDataRepository.findByCode(code.getValue())
            .map(this::toDomain);
    }

    @Override
    public List<Country> findAll() {
        return springDataRepository.findAll().stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public boolean existsByCode(CountryCode code) {
        return springDataRepository.existsByCode(code.getValue());
    }

    @Override
    public boolean existsByCurrencyId(Long currencyId) {
        return springDataRepository.existsByCurrencyId(currencyId);
    }

    @Override
    public void deleteById(Long id) {
        springDataRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        springDataRepository.deleteAll();
    }

    private CountryEntity toEntity(Country domain) {
        CountryPrimitives primitives = domain.toPrimitives();
        return CountryEntity.builder()
            .id(primitives.getId())
            .code(primitives.getCode())
            .name(primitives.getName())
            .currencyId(primitives.getCurrencyId())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }

    private Country toDomain(@NonNull CountryEntity entity) {
        CountryPrimitives primitives = CountryPrimitives.builder()
            .id(entity.getId())
            .code(entity.getCode())
            .name(entity.getName())
            .currencyId(entity.getCurrencyId())
            .build();
        
        return new Country(
            primitives.getId() != null ? new CountryId(primitives.getId()) : null,
            new CountryCode(primitives.getCode()),
            new CountryName(primitives.getName()),
            new CurrencyId(primitives.getCurrencyId()),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}

