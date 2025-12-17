package com.savinco.financial.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Financial data response with values in original currency")
public class FinancialDataResponse {
    
    @Schema(description = "Country code", example = "ESP")
    private String countryCode;
    
    @Schema(description = "Country name", example = "España")
    private String countryName;
    
    @Schema(description = "Original currency code", example = "EUR")
    private String originalCurrency;
    
    @Schema(description = "Capital saved in original currency", example = "1000000.00")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "#0.00")
    private BigDecimal capitalSaved;
    
    @Schema(description = "Capital loaned in original currency", example = "5000000.00")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "#0.00")
    private BigDecimal capitalLoaned;
    
    @Schema(description = "Profits generated in original currency", example = "500000.00")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "#0.00")
    private BigDecimal profitsGenerated;
    
    @Schema(description = "Total amount in original currency (capitalSaved + capitalLoaned + profitsGenerated)", example = "6500000.00")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "#0.00")
    private BigDecimal totalInUSD;
}
