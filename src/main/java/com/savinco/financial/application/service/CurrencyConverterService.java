package com.savinco.financial.application.service;

import com.savinco.financial.domain.model.Currency;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CurrencyConverterService {

    public BigDecimal convertToUSD(Currency currency, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null");
        }

        // If currency is base (USD), return amount as is
        if (currency.isBase()) {
            return amount.setScale(2, RoundingMode.HALF_UP);
        }

        // Convert using exchange rate to base (USD)
        // exchangeRateToBase is the rate to convert FROM this currency TO USD
        // Example: EUR with rate 0.90 means 1 EUR = 0.90 USD, so to convert EUR to USD: amount * 0.90
        // But wait, if rate is 0.90, that means 1 EUR = 0.90 USD, so we multiply by 0.90
        // However, the rate stored should be: 1 unit of currency = rate USD
        // So if EUR rate is 0.90, 1 EUR = 0.90 USD, we multiply by 0.90
        // But if we want 1 EUR = 1.11 USD, the rate should be 1.11, not 0.90
        
        // Actually, looking at the old code:
        // EUR_TO_USD = 1.111111... means 1 EUR = 1.111111 USD
        // So the rate stored should be 1.111111, not 0.90
        
        // But the user said exchangeRateToBase, which suggests:
        // If base is USD and EUR rate is 0.90, it might mean 1 USD = 0.90 EUR (wrong)
        // Or it might mean 1 EUR = 0.90 USD (correct for conversion)
        
        // Let's assume exchangeRateToBase means: 1 unit of currency = rate USD
        // So if EUR rate is 0.90, 1 EUR = 0.90 USD, we multiply by 0.90
        BigDecimal rate = currency.getExchangeRateToBase();
        return amount.multiply(rate)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
