package com.savinco.financial.application.dto;

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
@Schema(description = "Country information response")
public class CountryResponse {
    
    @Schema(description = "Country ID", example = "1")
    private Long id;
    
    @Schema(description = "Country code", example = "ECU")
    private String code;
    
    @Schema(description = "Country name", example = "Ecuador")
    private String name;
    
    @Schema(description = "Currency code for this country", example = "USD")
    private String currencyCode;
    
    @Schema(description = "Timestamp when country was created")
    private LocalDateTime createdAt;
    
    @Schema(description = "Timestamp when country was last updated")
    private LocalDateTime updatedAt;
}

