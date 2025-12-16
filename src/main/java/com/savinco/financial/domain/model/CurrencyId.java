package com.savinco.financial.domain.model;

import lombok.Value;

@Value
public class CurrencyId {
    Long value;

    public CurrencyId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("CurrencyId cannot be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("CurrencyId must be positive");
        }
        this.value = value;
    }
}

