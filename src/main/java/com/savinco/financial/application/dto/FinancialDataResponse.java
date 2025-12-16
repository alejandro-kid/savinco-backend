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
@Schema(description = "Financial data response with values converted to USD")
public class FinancialDataResponse {
    
    @Schema(description = "Country code", example = "ESP")
    private String countryCode;
    
    @Schema(description = "Country name", example = "España")
    private String countryName;
    
    @Schema(description = "Original currency code", example = "EUR")
    private String originalCurrency;
    
    @Schema(description = "Capital saved converted to USD", example = "1111111.11")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "#0.00")
    private BigDecimal capitalSaved;
    
    @Schema(description = "Capital loaned converted to USD", example = "5555555.56")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "#0.00")
    private BigDecimal capitalLoaned;
    
    @Schema(description = "Profits generated converted to USD", example = "555555.56")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "#0.00")
    private BigDecimal profitsGenerated;
    
    @Schema(description = "Total amount in USD (capitalSaved + capitalLoaned + profitsGenerated)", example = "7222222.23")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "#0.00")
    private BigDecimal totalInUSD;
}
