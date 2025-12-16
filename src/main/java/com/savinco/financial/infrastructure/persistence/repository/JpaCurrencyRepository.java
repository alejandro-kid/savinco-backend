package com.savinco.financial.infrastructure.persistence.repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.savinco.financial.domain.model.Currency;
import com.savinco.financial.domain.model.CurrencyCode;
import com.savinco.financial.domain.model.CurrencyId;
import com.savinco.financial.domain.model.CurrencyName;
import com.savinco.financial.domain.model.CurrencyPrimitives;
import com.savinco.financial.domain.repository.CurrencyRepository;
import com.savinco.financial.infrastructure.persistence.entity.CurrencyEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JpaCurrencyRepository implements CurrencyRepository {

    private final SpringDataCurrencyRepository springDataRepository;

    @Override
    @SuppressWarnings("null")
    public Currency save(Currency currency) {
        CurrencyEntity entity = toEntity(currency);
        CurrencyEntity saved = Objects.requireNonNull(
            springDataRepository.save(entity),
            "Failed to save currency entity"
        );
        return toDomain(saved);
    }

    @Override
    public Optional<Currency> findById(Long id) {
        return springDataRepository.findById(id)
            .map(this::toDomain);
    }

    @Override
    public Optional<Currency> findByCode(CurrencyCode code) {
        return springDataRepository.findByCode(code.getValue())
            .map(this::toDomain);
    }

    @Override
    public List<Currency> findAll() {
        return springDataRepository.findAll().stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public Optional<Currency> findBaseCurrency() {
        return springDataRepository.findByIsBaseTrue()
            .map(this::toDomain);
    }

    @Override
    public boolean existsByCode(CurrencyCode code) {
        return springDataRepository.existsByCode(code.getValue());
    }

    @Override
    public void deleteById(Long id) {
        springDataRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        springDataRepository.deleteAll();
    }

    private CurrencyEntity toEntity(Currency domain) {
        CurrencyPrimitives primitives = domain.toPrimitives();
        return CurrencyEntity.builder()
            .id(primitives.getId())
            .code(primitives.getCode())
            .name(primitives.getName())
            .isBase(primitives.getIsBase())
            .exchangeRateToBase(primitives.getExchangeRateToBase())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }

    private Currency toDomain(@NonNull CurrencyEntity entity) {
        CurrencyPrimitives primitives = CurrencyPrimitives.builder()
            .id(entity.getId())
            .code(entity.getCode())
            .name(entity.getName())
            .isBase(entity.getIsBase())
            .exchangeRateToBase(entity.getExchangeRateToBase())
            .build();
        
        return new Currency(
            primitives.getId() != null ? new CurrencyId(primitives.getId()) : null,
            new CurrencyCode(primitives.getCode()),
            new CurrencyName(primitives.getName()),
            primitives.getIsBase(),
            primitives.getExchangeRateToBase(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}

