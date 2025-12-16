package com.savinco.financial.domain.model;

import lombok.Value;

@Value
public class CurrencyCode {
    String value;

    public CurrencyCode(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CurrencyCode cannot be null or blank");
        }
        if (value.length() != 3) {
            throw new IllegalArgumentException("CurrencyCode must be exactly 3 characters");
        }
        if (!value.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("CurrencyCode must be 3 uppercase letters");
        }
        this.value = value;
    }
}

