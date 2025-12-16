package com.savinco.financial.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.savinco.financial.application.dto.CurrencyRequest;
import com.savinco.financial.application.dto.CurrencyResponse;
import com.savinco.financial.application.dto.UpdateExchangeRateRequest;
import com.savinco.financial.application.service.CurrencyService;
import com.savinco.financial.domain.model.Currency;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/currencies")
@RequiredArgsConstructor
@Tag(name = "Currency", description = "API for managing currencies and exchange rates")
public class CurrencyController {

    private final CurrencyService currencyService;

    @PostMapping
    @Operation(summary = "Create currency", description = "Create a new currency with exchange rate to base currency (USD)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Currency created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data or base currency already exists"),
        @ApiResponse(responseCode = "409", description = "Currency already exists with this code")
    })
    public ResponseEntity<CurrencyResponse> create(@Valid @RequestBody CurrencyRequest request) {
        Currency currency = currencyService.create(
            request.getCode(),
            request.getName(),
            request.getIsBase(),
            request.getExchangeRateToBase()
        );
        CurrencyResponse response = toResponse(currency);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all currencies", description = "Retrieve all currencies with their exchange rates")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Currencies retrieved successfully")
    })
    public ResponseEntity<List<CurrencyResponse>> findAll() {
        List<Currency> currencies = currencyService.findAll();
        List<CurrencyResponse> response = currencies.stream()
            .map(this::toResponse)
            .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{code}")
    @Operation(summary = "Get currency by code", description = "Retrieve currency information by code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Currency retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid currency code format"),
        @ApiResponse(responseCode = "404", description = "Currency not found")
    })
    public ResponseEntity<CurrencyResponse> findByCode(
            @Parameter(description = "Currency code (USD, EUR, PEN, NPR)", example = "EUR", required = true)
            @PathVariable String code) {
        Currency currency = currencyService.findByCode(code);
        CurrencyResponse response = toResponse(currency);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/base")
    @Operation(summary = "Get base currency", description = "Retrieve the base currency (USD)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Base currency retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Base currency not found")
    })
    public ResponseEntity<CurrencyResponse> getBaseCurrency() {
        Currency currency = currencyService.getBaseCurrency();
        CurrencyResponse response = toResponse(currency);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{code}/exchange-rate")
    @Operation(summary = "Update exchange rate", description = "Update the exchange rate for a non-base currency")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exchange rate updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data or cannot update base currency rate"),
        @ApiResponse(responseCode = "404", description = "Currency not found")
    })
    public ResponseEntity<CurrencyResponse> updateExchangeRate(
            @Parameter(description = "Currency code (EUR, PEN, NPR)", example = "EUR", required = true)
            @PathVariable String code,
            @Valid @RequestBody UpdateExchangeRateRequest request) {
        Currency currency = currencyService.updateExchangeRate(code, request.getExchangeRateToBase());
        CurrencyResponse response = toResponse(currency);
        return ResponseEntity.ok(response);
    }

    private CurrencyResponse toResponse(Currency currency) {
        return CurrencyResponse.builder()
            .id(currency.getId() != null ? currency.getId().getValue() : null)
            .code(currency.getCode().getValue())
            .name(currency.getName().getValue())
            .isBase(currency.isBase())
            .exchangeRateToBase(currency.getExchangeRateToBase())
            .createdAt(currency.getCreatedAt())
            .updatedAt(currency.getUpdatedAt())
            .build();
    }
}

