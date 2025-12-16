package com.savinco.financial.domain.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class Currency {
    private final CurrencyId id;
    private final CurrencyCode code;
    private final CurrencyName name;
    private final boolean isBase;
    private BigDecimal exchangeRateToBase;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    // Factory method for new instances
    public static Currency create(CurrencyPrimitives primitives) {
        validateExchangeRate(primitives.getIsBase(), primitives.getExchangeRateToBase());
        
        Currency currency = new Currency(
            primitives.getId() != null ? new CurrencyId(primitives.getId()) : null,
            new CurrencyCode(primitives.getCode()),
            new CurrencyName(primitives.getName()),
            primitives.getIsBase(),
            primitives.getExchangeRateToBase(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        return currency;
    }

    // Constructor for hydration from DB (no events)
    public Currency(CurrencyId id, CurrencyCode code, CurrencyName name, 
                   boolean isBase, BigDecimal exchangeRateToBase,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        validateExchangeRate(isBase, exchangeRateToBase);
        
        this.id = id;
        this.code = code;
        this.name = name;
        this.isBase = isBase;
        this.exchangeRateToBase = exchangeRateToBase;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void updateExchangeRate(BigDecimal newRate) {
        if (isBase) {
            throw new IllegalStateException("Cannot update exchange rate for base currency");
        }
        if (newRate == null) {
            throw new IllegalArgumentException("Exchange rate cannot be null");
        }
        if (newRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Exchange rate must be positive");
        }
        this.exchangeRateToBase = newRate;
    }

    public CurrencyPrimitives toPrimitives() {
        return CurrencyPrimitives.builder()
            .id(id != null ? id.getValue() : null)
            .code(code.getValue())
            .name(name.getValue())
            .isBase(isBase)
            .exchangeRateToBase(exchangeRateToBase)
            .build();
    }

    private static void validateExchangeRate(boolean isBase, BigDecimal exchangeRate) {
        if (exchangeRate == null) {
            throw new IllegalArgumentException("Exchange rate cannot be null");
        }
        if (isBase) {
            if (exchangeRate.compareTo(BigDecimal.ONE) != 0) {
                throw new IllegalArgumentException("Base currency exchange rate must be 1.00");
            }
        } else {
            if (exchangeRate.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Exchange rate must be positive for non-base currency");
            }
        }
    }
}
