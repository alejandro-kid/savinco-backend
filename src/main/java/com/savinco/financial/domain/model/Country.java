package com.savinco.financial.domain.model;

import lombok.Getter;

@Getter
public enum Country {
    ECU("ECU", "Ecuador", Currency.USD),
    ESP("ESP", "España", Currency.EUR),
    PER("PER", "Perú", Currency.PEN),
    NPL("NPL", "Nepal", Currency.NPR);

    private final String code;
    private final String name;
    private final Currency currency;

    Country(String code, String name, Currency currency) {
        this.code = code;
        this.name = name;
        this.currency = currency;
    }

    public static Country fromCode(String code) {
        for (Country country : values()) {
            if (country.code.equals(code)) {
                return country;
            }
        }
        return null;
    }

    public boolean isValidCurrency(Currency currency) {
        return this.currency == currency;
    }
}
