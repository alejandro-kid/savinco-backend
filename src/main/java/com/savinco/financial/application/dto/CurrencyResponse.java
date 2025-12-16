package com.savinco.financial.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Currency information response")
public class CurrencyResponse {
    
    @Schema(description = "Currency ID", example = "1")
    private Long id;
    
    @Schema(description = "Currency code", example = "EUR")
    private String code;
    
    @Schema(description = "Currency name", example = "Euro")
    private String name;
    
    @Schema(description = "Indicates if this is the base currency", example = "false")
    private Boolean isBase;
    
    @Schema(description = "Exchange rate to base currency (USD)", example = "0.90")
    private BigDecimal exchangeRateToBase;
    
    @Schema(description = "Timestamp when currency was created")
    private LocalDateTime createdAt;
    
    @Schema(description = "Timestamp when currency was last updated")
    private LocalDateTime updatedAt;
}

