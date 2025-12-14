package com.savinco.financial.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Consolidated summary of all financial data with totals in USD")
public class ConsolidatedSummaryResponse {
    
    @Schema(description = "Total capital saved across all countries in USD", example = "33666477.00")
    private BigDecimal totalCapitalSaved;
    
    @Schema(description = "Total capital loaned across all countries in USD", example = "274878091.00")
    private BigDecimal totalCapitalLoaned;
    
    @Schema(description = "Total profits generated across all countries in USD", example = "39581411.00")
    private BigDecimal totalProfitsGenerated;
    
    @Schema(description = "Grand total (totalCapitalSaved + totalCapitalLoaned + totalProfitsGenerated) in USD", example = "348125979.00")
    private BigDecimal grandTotal;
    
    @Schema(description = "Financial data breakdown by country")
    private List<CountrySummary> byCountry;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Financial data summary for a single country")
    public static class CountrySummary {
        
        @Schema(description = "Country code", example = "ESP")
        private String countryCode;
        
        @Schema(description = "Country name", example = "España")
        private String countryName;
        
        @Schema(description = "Capital saved in USD", example = "1111111.11")
        private BigDecimal capitalSaved;
        
        @Schema(description = "Capital loaned in USD", example = "5555555.56")
        private BigDecimal capitalLoaned;
        
        @Schema(description = "Profits generated in USD", example = "555555.56")
        private BigDecimal profitsGenerated;
    }
}