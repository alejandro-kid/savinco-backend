package com.savinco.financial.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create or update financial data for a country")
public class FinancialDataRequest {
    
    @NotBlank(message = "Country code is required")
    @Schema(description = "Country code (ECU, ESP, PER, NPL)", example = "ESP", required = true)
    private String countryCode;

    @NotBlank(message = "Currency code is required")
    @Schema(description = "Currency code (USD, EUR, PEN, NPR). Must match country: ECU=USD, ESP=EUR, PER=PEN, NPL=NPR", 
            example = "EUR", required = true)
    private String currencyCode;

    @NotNull(message = "Capital saved is required")
    @DecimalMin(value = "0.0", message = "Capital saved must be non-negative")
    @Schema(description = "Capital saved in original currency", example = "1000000.00", required = true)
    private BigDecimal capitalSaved;

    @NotNull(message = "Capital loaned is required")
    @DecimalMin(value = "0.0", message = "Capital loaned must be non-negative")
    @Schema(description = "Capital loaned in original currency", example = "5000000.00", required = true)
    private BigDecimal capitalLoaned;

    @NotNull(message = "Profits generated is required")
    @DecimalMin(value = "0.0", message = "Profits generated must be non-negative")
    @Schema(description = "Profits generated in original currency", example = "500000.00", required = true)
    private BigDecimal profitsGenerated;
}
