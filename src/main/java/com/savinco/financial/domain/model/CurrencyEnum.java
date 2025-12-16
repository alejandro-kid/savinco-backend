package com.savinco.financial.domain.model;

import lombok.Getter;

/**
 * @deprecated Use {@link Currency} entity instead. This enum is kept for backward compatibility
 * and will be removed after migration is complete.
 */
@Deprecated
@Getter
public enum CurrencyEnum {
    USD("USD", "US Dollar"),
    EUR("EUR", "Euro"),
    PEN("PEN", "Peruvian Sol"),
    NPR("NPR", "Nepalese Rupee");

    private final String code;
    private final String name;

    CurrencyEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static CurrencyEnum fromCode(String code) {
        for (CurrencyEnum currency : values()) {
            if (currency.code.equals(code)) {
                return currency;
            }
        }
        return null;
    }
}
