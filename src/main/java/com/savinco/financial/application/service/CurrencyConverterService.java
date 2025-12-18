package com.savinco.financial.application.service;

import com.savinco.financial.domain.model.Currency;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CurrencyConverterService {

    public BigDecimal convertToUSD(Currency currency, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            log.debug("Converting zero or null amount to USD");
            return BigDecimal.ZERO;
        }

        if (currency == null) {
            log.error("Currency conversion failed: currency is null");
            throw new IllegalArgumentException("Currency cannot be null");
        }

        // If currency is base (USD), return amount as is
        if (currency.isBase()) {
            log.debug("Currency is base (USD), returning amount as is: amount={}", amount);
            return amount.setScale(2, RoundingMode.HALF_UP);
        }

        // Convert using exchange rate to base (USD)
        BigDecimal rate = currency.getExchangeRateToBase();
        BigDecimal converted = amount.multiply(rate)
                .setScale(2, RoundingMode.HALF_UP);
        
        log.debug("Converted {} {} to {} USD (rate: {})", 
            amount, currency.getCode().getValue(), converted, rate);
        
        return converted;
    }
}
