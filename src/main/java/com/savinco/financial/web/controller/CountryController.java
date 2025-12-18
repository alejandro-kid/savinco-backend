package com.savinco.financial.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.savinco.financial.application.dto.CountryRequest;
import com.savinco.financial.application.dto.CountryResponse;
import com.savinco.financial.application.service.CountryService;
import com.savinco.financial.domain.model.Country;
import com.savinco.financial.domain.model.Currency;
import com.savinco.financial.domain.model.CurrencyId;
import com.savinco.financial.domain.repository.CurrencyRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/countries")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Country", description = "API for managing countries and their associated currencies")
public class CountryController {

    private final CountryService countryService;
    private final CurrencyRepository currencyRepository;

    @PostMapping
    @Operation(summary = "Create country", description = "Create a new country with an associated currency")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Country created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Currency not found"),
        @ApiResponse(responseCode = "409", description = "Country already exists with this code")
    })
    public ResponseEntity<CountryResponse> create(@Valid @RequestBody CountryRequest request) {
        log.info("Creating country: code={}, name={}, currencyCode={}", 
            request.getCode(), request.getName(), request.getCurrencyCode());
        
        Country country = countryService.create(
            request.getCode(),
            request.getName(),
            request.getCurrencyCode()
        );
        CountryResponse response = toResponse(country);
        
        log.info("Country created successfully: id={}, code={}", 
            response.getId(), response.getCode());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all countries", description = "Retrieve all countries with their associated currencies")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Countries retrieved successfully")
    })
    public ResponseEntity<List<CountryResponse>> findAll() {
        log.debug("Finding all countries");
        List<Country> countries = countryService.findAll();
        List<CountryResponse> response = countries.stream()
            .map(this::toResponse)
            .toList();
        
        log.info("Found {} countries", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{code}")
    @Operation(summary = "Get country by code", description = "Retrieve country information by code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Country retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid country code format"),
        @ApiResponse(responseCode = "404", description = "Country not found")
    })
    public ResponseEntity<CountryResponse> findByCode(
            @Parameter(description = "Country code (ECU, ESP, PER, NPL)", example = "ECU", required = true)
            @PathVariable String code) {
        log.debug("Finding country by code: {}", code);
        Country country = countryService.findByCode(code);
        CountryResponse response = toResponse(country);
        
        log.info("Country found: code={}, id={}", code, response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{code}")
    @Operation(summary = "Delete country", description = "Delete a country. Cannot delete if financial data is associated with it.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Country deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid country code format"),
        @ApiResponse(responseCode = "404", description = "Country not found"),
        @ApiResponse(responseCode = "409", description = "Cannot delete country: financial data is associated with it")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Country code (ECU, ESP, PER, NPL)", example = "ECU", required = true)
            @PathVariable String code) {
        log.info("Deleting country: code={}", code);
        countryService.delete(code);
        
        log.info("Country deleted successfully: code={}", code);
        return ResponseEntity.noContent().build();
    }

    private CountryResponse toResponse(Country country) {
        // Fetch currency to get the code
        Currency currency = currencyRepository.findById(country.getCurrencyId().getValue())
            .orElseThrow(() -> new IllegalStateException("Currency not found with ID: " + country.getCurrencyId().getValue()));
        
        return CountryResponse.builder()
            .id(country.getId() != null ? country.getId().getValue() : null)
            .code(country.getCode().getValue())
            .name(country.getName().getValue())
            .currencyCode(currency.getCode().getValue())
            .createdAt(country.getCreatedAt())
            .updatedAt(country.getUpdatedAt())
            .build();
    }
}

