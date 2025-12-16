package com.savinco.financial.domain.model;

import lombok.Value;

@Value
public class CountryId {
    Long value;

    public CountryId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("CountryId cannot be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("CountryId must be positive");
        }
        this.value = value;
    }
}

