package com.savinco.financial.domain.model;

import lombok.Getter;

/**
 * @deprecated Use {@link Country} entity instead. This enum is kept for backward compatibility
 * and will be removed after migration is complete.
 */
@Deprecated
@Getter
public enum CountryEnum {
    ECU("ECU", "Ecuador", CurrencyEnum.USD),
    ESP("ESP", "España", CurrencyEnum.EUR),
    PER("PER", "Perú", CurrencyEnum.PEN),
    NPL("NPL", "Nepal", CurrencyEnum.NPR);

    private final String code;
    private final String name;
    private final CurrencyEnum currency;

    CountryEnum(String code, String name, CurrencyEnum currency) {
        this.code = code;
        this.name = name;
        this.currency = currency;
    }

    public static CountryEnum fromCode(String code) {
        for (CountryEnum country : values()) {
            if (country.code.equals(code)) {
                return country;
            }
        }
        return null;
    }

    public boolean isValidCurrency(CurrencyEnum currency) {
        return this.currency == currency;
    }
}
