package com.savinco.financial.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Request to create a country")
public class CountryRequest {
    
    @NotBlank(message = "Country code is required")
    @Size(min = 3, max = 3, message = "Country code must be exactly 3 characters")
    @Pattern(regexp = "[A-Z]{3}", message = "Country code must be 3 uppercase letters")
    @Schema(description = "ISO country code (3 uppercase letters)", example = "ECU", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @NotBlank(message = "Country name is required")
    @Schema(description = "Country name", example = "Ecuador", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "Currency code is required")
    @Size(min = 3, max = 3, message = "Currency code must be exactly 3 characters")
    @Pattern(regexp = "[A-Z]{3}", message = "Currency code must be 3 uppercase letters")
    @Schema(description = "ISO currency code for this country (3 uppercase letters)", example = "USD", requiredMode = Schema.RequiredMode.REQUIRED)
    private String currencyCode;
}

