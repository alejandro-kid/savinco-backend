package com.savinco.financial.application.service;

import com.savinco.financial.domain.model.Currency;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CurrencyConverterService {

    // Fixed exchange rates (more precise for accurate conversions)
    // 1 USD = 0.90 EUR → 1 EUR = 1/0.90 = 1.111111... USD
    // 1 USD = 3.3 PEN → 1 PEN = 1/3.3 = 0.303030... USD
    // 1 USD = 133 NPR → 1 NPR = 1/133 = 0.007518... USD
    private static final BigDecimal EUR_TO_USD = new BigDecimal("1.111111111111111111");
    private static final BigDecimal PEN_TO_USD = new BigDecimal("0.303030303030303030");
    private static final BigDecimal NPR_TO_USD = new BigDecimal("0.007518796992481203");
    private static final BigDecimal USD_TO_USD = BigDecimal.ONE;

    public BigDecimal convertToUSD(Currency currency, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal rate = getExchangeRate(currency);
        return amount.multiply(rate)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getExchangeRate(Currency currency) {
        return switch (currency) {
            case USD -> USD_TO_USD;
            case EUR -> EUR_TO_USD;
            case PEN -> PEN_TO_USD;
            case NPR -> NPR_TO_USD;
        };
    }
}
