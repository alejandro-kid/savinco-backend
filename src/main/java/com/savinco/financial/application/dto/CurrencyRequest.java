package com.savinco.financial.application.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a currency")
public class CurrencyRequest {
    
    @NotBlank(message = "Currency code is required")
    @Size(min = 3, max = 3, message = "Currency code must be exactly 3 characters")
    @Pattern(regexp = "[A-Z]{3}", message = "Currency code must be 3 uppercase letters")
    @Schema(description = "ISO currency code (3 uppercase letters)", example = "EUR", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @NotBlank(message = "Currency name is required")
    @Schema(description = "Currency name", example = "Euro", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotNull(message = "isBase is required")
    @Schema(description = "Indicates if this is the base currency (USD). Only one currency can be base.", 
            example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isBase;

    @NotNull(message = "Exchange rate to base is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Exchange rate must be positive")
    @Schema(description = "Exchange rate to convert 1 unit of this currency to base currency (USD). Base currency rate must be 1.00", 
            example = "0.90", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal exchangeRateToBase;
}

