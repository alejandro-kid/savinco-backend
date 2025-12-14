package com.savinco.financial.domain.model;

import lombok.Getter;

@Getter
public enum Currency {
    USD("USD", "US Dollar"),
    EUR("EUR", "Euro"),
    PEN("PEN", "Peruvian Sol"),
    NPR("NPR", "Nepalese Rupee");

    private final String code;
    private final String name;

    Currency(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Currency fromCode(String code) {
        for (Currency currency : values()) {
            if (currency.code.equals(code)) {
                return currency;
            }
        }
        return null;
    }
}
