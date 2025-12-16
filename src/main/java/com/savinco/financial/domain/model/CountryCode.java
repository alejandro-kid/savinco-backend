package com.savinco.financial.domain.model;

import lombok.Value;

@Value
public class CountryCode {
    String value;

    public CountryCode(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CountryCode cannot be null or blank");
        }
        if (value.length() != 3) {
            throw new IllegalArgumentException("CountryCode must be exactly 3 characters");
        }
        if (!value.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("CountryCode must be 3 uppercase letters");
        }
        this.value = value;
    }
}

