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

import com.savinco.financial.application.dto.ConsolidatedSummaryResponse;
import com.savinco.financial.application.dto.FinancialDataRequest;
import com.savinco.financial.application.dto.FinancialDataResponse;
import com.savinco.financial.application.service.FinancialDataService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/financial-data")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Financial Data", description = "API for managing financial data by country")
public class FinancialDataController {

    private final FinancialDataService financialDataService;

    @PostMapping
    @Operation(summary = "Create financial data", description = "Create a new financial data record for a country")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Financial data created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "409", description = "Financial data already exists for this country")
    })
    public ResponseEntity<FinancialDataResponse> create(@Valid @RequestBody FinancialDataRequest request) {
        log.info("Creating financial data: countryCode={}, currencyCode={}, capitalSaved={}, capitalLoaned={}, profitsGenerated={}", 
            request.getCountryCode(), request.getCurrencyCode(), 
            request.getCapitalSaved(), request.getCapitalLoaned(), request.getProfitsGenerated());
        
        FinancialDataResponse response = financialDataService.create(request);
        
        log.info("Financial data created successfully: countryCode={}", response.getCountryCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all financial data", description = "Retrieve all financial data records with values in original currency")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Financial data retrieved successfully")
    })
    public ResponseEntity<List<FinancialDataResponse>> findAll() {
        log.debug("Finding all financial data");
        List<FinancialDataResponse> response = financialDataService.findAll();
        
        log.info("Found {} financial data records", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{countryCode}")
    @Operation(summary = "Get financial data by country", description = "Retrieve financial data for a specific country with values in original currency")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Financial data retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid country code"),
        @ApiResponse(responseCode = "404", description = "Financial data not found for this country")
    })
    public ResponseEntity<FinancialDataResponse> findByCountryCode(
            @Parameter(description = "Country code (ECU, ESP, PER, NPL)", example = "ESP", required = true)
            @PathVariable String countryCode) {
        log.debug("Finding financial data by country code: {}", countryCode);
        FinancialDataResponse response = financialDataService.findByCountryCode(countryCode);
        
        log.info("Financial data found: countryCode={}", countryCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    @Operation(summary = "Get consolidated summary", description = "Retrieve consolidated summary of all financial data with totals in USD")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Summary retrieved successfully")
    })
    public ResponseEntity<ConsolidatedSummaryResponse> getSummary() {
        log.debug("Getting consolidated summary");
        ConsolidatedSummaryResponse response = financialDataService.getSummary();
        
        log.info("Consolidated summary generated: grandTotal={}, countriesCount={}", 
            response.getGrandTotal(), response.getByCountry().size());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{countryCode}")
    @Operation(summary = "Update financial data", description = "Update financial data for an existing country")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Financial data updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data or country code mismatch"),
        @ApiResponse(responseCode = "404", description = "Financial data not found for this country")
    })
    public ResponseEntity<FinancialDataResponse> update(
            @Parameter(description = "Country code (ECU, ESP, PER, NPL)", example = "ESP", required = true)
            @PathVariable String countryCode,
            @Valid @RequestBody FinancialDataRequest request) {
        log.info("Updating financial data: countryCode={}, capitalSaved={}, capitalLoaned={}, profitsGenerated={}", 
            countryCode, request.getCapitalSaved(), request.getCapitalLoaned(), request.getProfitsGenerated());
        
        FinancialDataResponse response = financialDataService.update(countryCode, request);
        
        log.info("Financial data updated successfully: countryCode={}", countryCode);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{countryCode}")
    @Operation(summary = "Delete financial data", description = "Delete financial data for a country")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Financial data deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid country code"),
        @ApiResponse(responseCode = "404", description = "Financial data not found for this country")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Country code (ECU, ESP, PER, NPL)", example = "ESP", required = true)
            @PathVariable String countryCode) {
        log.info("Deleting financial data: countryCode={}", countryCode);
        financialDataService.delete(countryCode);
        
        log.info("Financial data deleted successfully: countryCode={}", countryCode);
        return ResponseEntity.noContent().build();
    }
}
