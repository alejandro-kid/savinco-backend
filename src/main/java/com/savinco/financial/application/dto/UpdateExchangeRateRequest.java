package com.savinco.financial.application.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update currency exchange rate")
public class UpdateExchangeRateRequest {
    
    @NotNull(message = "Exchange rate to base is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Exchange rate must be positive")
    @Schema(description = "New exchange rate to convert 1 unit of this currency to base currency (USD)", 
            example = "0.95", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal exchangeRateToBase;
}

