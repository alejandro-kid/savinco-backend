package com.savinco.financial.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CountryPrimitives {
    Long id;
    String code;
    String name;
    Long currencyId;
}

