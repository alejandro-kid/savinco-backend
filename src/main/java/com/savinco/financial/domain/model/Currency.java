package com.savinco.financial.domain.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a currency with its exchange rate to the base currency (USD).
 * 
 * The exchangeRateToBase field represents: 1 USD (base currency) = X units of this currency
 * 
 * Examples:
 * - USD (base currency): exchangeRateToBase = 1.00 (1 USD = 1 USD)
 * - EUR: exchangeRateToBase = 0.90 (1 USD = 0.90 EUR)
 * - PEN: exchangeRateToBase = 0.30 (1 USD = 0.30 PEN)
 * 
 * To convert an amount from this currency to USD:
 * amountInUSD = amountInThisCurrency / exchangeRateToBase
 */
@Getter
public class Currency {
    private final CurrencyId id;
    private final CurrencyCode code;
    private final CurrencyName name;
    private final boolean isBase;
    
    /**
     * Exchange rate from base currency (USD) to this currency.
     * Represents: 1 USD = exchangeRateToBase units of this currency
     * 
     * For base currency: must be 1.00
     * For other currencies: must be > 0
     * 
     * Example: If exchangeRateToBase = 0.90 for EUR, then 1 USD = 0.90 EUR
     */
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

    /**
     * Updates the exchange rate from base currency (USD) to this currency.
     * 
     * The newRate represents: 1 USD = newRate units of this currency
     * 
     * @param newRate The new exchange rate (must be > 0)
     * @throws IllegalStateException if this is the base currency (base currency rate is always 1.00)
     * @throws IllegalArgumentException if newRate is null or <= 0
     */
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
