package com.savinco.financial.domain.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CurrencyPrimitives {
    Long id;
    String code;
    String name;
    Boolean isBase;
    BigDecimal exchangeRateToBase;
}

