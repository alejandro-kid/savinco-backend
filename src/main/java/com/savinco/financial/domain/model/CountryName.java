package com.savinco.financial.domain.model;

import lombok.Value;

@Value
public class CountryName {
    String value;

    public CountryName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CountryName cannot be null or blank");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("CountryName cannot exceed 100 characters");
        }
        this.value = value;
    }
}

