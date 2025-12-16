package com.savinco.financial.domain.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class FinancialData {
    private final Long id;
    private final Country country;
    private final Currency currency;
    private final BigDecimal capitalSaved;
    private final BigDecimal capitalLoaned;
    private final BigDecimal profitsGenerated;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public FinancialData(Long id, Country country, Currency currency, 
                         BigDecimal capitalSaved, BigDecimal capitalLoaned, 
                         BigDecimal profitsGenerated, Timestamps timestamps) {
        this.id = id;
        this.country = country;
        this.currency = currency;
        this.capitalSaved = capitalSaved;
        this.capitalLoaned = capitalLoaned;
        this.profitsGenerated = profitsGenerated;
        this.createdAt = timestamps.createdAt();
        this.updatedAt = timestamps.updatedAt();
    }

    public record Timestamps(LocalDateTime createdAt, LocalDateTime updatedAt) {
    }

    public FinancialData(Country country, Currency currency, 
                         BigDecimal capitalSaved, BigDecimal capitalLoaned, 
                         BigDecimal profitsGenerated) {
        this(null, country, currency, capitalSaved, capitalLoaned, profitsGenerated, 
             new Timestamps(LocalDateTime.now(), LocalDateTime.now()));
    }

    public void validate() {
        if (country == null) {
            throw new IllegalArgumentException("Country cannot be null");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null");
        }
        if (!country.isValidCurrency(currency)) {
            throw new IllegalArgumentException(
                "Currency " + currency.getCode().getValue() + " does not match country " + country.getCode().getValue()
            );
        }
        if (capitalSaved == null || capitalSaved.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Capital saved must be non-negative");
        }
        if (capitalLoaned == null || capitalLoaned.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Capital loaned must be non-negative");
        }
        if (profitsGenerated == null || profitsGenerated.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Profits generated must be non-negative");
        }
    }
}
