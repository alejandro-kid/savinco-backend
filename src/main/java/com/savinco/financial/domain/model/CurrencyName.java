package com.savinco.financial.domain.model;

import lombok.Value;

@Value
public class CurrencyName {
    String value;

    public CurrencyName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CurrencyName cannot be null or blank");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("CurrencyName cannot exceed 100 characters");
        }
        this.value = value;
    }
}

