package com.savinco.financial.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Country {
    private final CountryId id;
    private final CountryCode code;
    private final CountryName name;
    private final CurrencyId currencyId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    // Factory method for new instances
    public static Country create(CountryPrimitives primitives) {
        if (primitives.getCurrencyId() == null) {
            throw new IllegalArgumentException("CurrencyId cannot be null");
        }
        
        Country country = new Country(
            primitives.getId() != null ? new CountryId(primitives.getId()) : null,
            new CountryCode(primitives.getCode()),
            new CountryName(primitives.getName()),
            new CurrencyId(primitives.getCurrencyId()),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        return country;
    }

    // Constructor for hydration from DB (no events)
    public Country(CountryId id, CountryCode code, CountryName name, 
                   CurrencyId currencyId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        if (currencyId == null) {
            throw new IllegalArgumentException("CurrencyId cannot be null");
        }
        
        this.id = id;
        this.code = code;
        this.name = name;
        this.currencyId = currencyId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public boolean isValidCurrency(Currency currency) {
        if (currency == null) {
            return false;
        }
        return this.currencyId.getValue().equals(currency.getId().getValue());
    }

    public CountryPrimitives toPrimitives() {
        return CountryPrimitives.builder()
            .id(id != null ? id.getValue() : null)
            .code(code.getValue())
            .name(name.getValue())
            .currencyId(currencyId.getValue())
            .build();
    }
}
