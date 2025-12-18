package com.savinco.financial.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/currencies")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Currency", description = "API for managing currencies and exchange rates")
public class CurrencyController {

    private final CurrencyService currencyService;

    @PostMapping
    @Operation(summary = "Create currency", 
               description = "Create a new currency with exchange rate to base currency. " +
                           "The first currency created automatically becomes the base currency with rate 1.00. " +
                           "Subsequent currencies cannot be marked as base. " +
                           "The base currency's exchange rate cannot be changed.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Currency created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "409", description = "Currency already exists with this code or attempt to create base currency when one already exists")
    })
    public ResponseEntity<CurrencyResponse> create(@Valid @RequestBody CurrencyRequest request) {
        log.info("Creating currency: code={}, name={}, isBase={}, exchangeRateToBase={}", 
            request.getCode(), request.getName(), request.getIsBase(), request.getExchangeRateToBase());
        
        Currency currency = currencyService.create(
            request.getCode(),
            request.getName(),
            request.getIsBase(),
            request.getExchangeRateToBase()
        );
        CurrencyResponse response = toResponse(currency);
        
        log.info("Currency created successfully: id={}, code={}, isBase={}", 
            response.getId(), response.getCode(), response.getIsBase());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all currencies", description = "Retrieve all currencies with their exchange rates")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Currencies retrieved successfully")
    })
    public ResponseEntity<List<CurrencyResponse>> findAll() {
        log.debug("Finding all currencies");
        List<Currency> currencies = currencyService.findAll();
        List<CurrencyResponse> response = currencies.stream()
            .map(this::toResponse)
            .toList();
        
        log.info("Found {} currencies", response.size());
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
        log.debug("Finding currency by code: {}", code);
        Currency currency = currencyService.findByCode(code);
        CurrencyResponse response = toResponse(currency);
        
        log.info("Currency found: code={}, id={}", code, response.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/base")
    @Operation(summary = "Get base currency", description = "Retrieve the base currency (USD)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Base currency retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Base currency not found")
    })
    public ResponseEntity<CurrencyResponse> getBaseCurrency() {
        log.debug("Finding base currency");
        Currency currency = currencyService.getBaseCurrency();
        CurrencyResponse response = toResponse(currency);
        
        log.info("Base currency found: code={}, id={}", response.getCode(), response.getId());
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
        log.info("Updating exchange rate: code={}, newRate={}", code, request.getExchangeRateToBase());
        
        Currency currency = currencyService.updateExchangeRate(code, request.getExchangeRateToBase());
        CurrencyResponse response = toResponse(currency);
        
        log.info("Exchange rate updated successfully: code={}, newRate={}", 
            code, response.getExchangeRateToBase());
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{code}")
    @Operation(summary = "Delete currency", 
               description = "Delete a currency. " +
                           "Cannot delete if countries are associated with it. " +
                           "Base currency can only be deleted if it is the only currency in the database.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Currency deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid currency code format"),
        @ApiResponse(responseCode = "404", description = "Currency not found"),
        @ApiResponse(responseCode = "409", description = "Cannot delete currency: countries are associated with it, or base currency cannot be deleted when other currencies exist")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Currency code (USD, EUR, PEN, NPR)", example = "EUR", required = true)
            @PathVariable String code) {
        log.info("Deleting currency: code={}", code);
        currencyService.delete(code);
        
        log.info("Currency deleted successfully: code={}", code);
        return ResponseEntity.noContent().build();
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

