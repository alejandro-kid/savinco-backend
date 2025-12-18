package com.savinco.financial.application.service;

import com.savinco.financial.domain.model.Currency;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CurrencyConverterService {

    /**
     * Converts an amount from the given currency to USD (base currency).
     * 
     * The exchangeRateToBase field represents: 1 USD (base currency) = X units of destination currency
     * Therefore, to convert from destination currency to USD, we divide: amountInUSD = amountInDestinationCurrency / exchangeRateToBase
     * 
     * Examples:
     * - If EUR has exchangeRateToBase = 0.90, then 1 USD = 0.90 EUR
     * - Converting 90 EUR to USD: 90 / 0.90 = 100 USD
     * - Converting 100 EUR to USD: 100 / 0.90 = 111.11 USD
     * - If PEN has exchangeRateToBase = 0.30, then 1 USD = 0.30 PEN
     * - Converting 30 PEN to USD: 30 / 0.30 = 100 USD
     * 
     * @param currency The source currency (must not be null)
     * @param amount The amount in source currency to convert
     * @return The equivalent amount in USD, rounded to 2 decimal places
     * @throws IllegalArgumentException if currency is null
     */
    public BigDecimal convertToUSD(Currency currency, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            log.debug("Converting zero or null amount to USD");
            return BigDecimal.ZERO;
        }

        if (currency == null) {
            log.error("Currency conversion failed: currency is null");
            throw new IllegalArgumentException("Currency cannot be null");
        }

        // If currency is base (USD), return amount as is (no conversion needed)
        if (currency.isBase()) {
            log.debug("Currency is base (USD), returning amount as is: amount={}", amount);
            return amount.setScale(2, RoundingMode.HALF_UP);
        }

        // Convert using exchange rate: 1 USD (base) = exchangeRateToBase units of destination currency
        // Formula: amountInUSD = amountInDestinationCurrency / exchangeRateToBase
        BigDecimal exchangeRateToBase = currency.getExchangeRateToBase();
        BigDecimal converted = amount.divide(exchangeRateToBase, 2, RoundingMode.HALF_UP);
        
        log.debug("Converted {} {} to {} USD (rate: 1 USD = {} {})", 
            amount, currency.getCode().getValue(), converted, 
            exchangeRateToBase, currency.getCode().getValue());
        
        return converted;
    }
}
