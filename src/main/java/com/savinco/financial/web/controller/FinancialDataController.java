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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/financial-data")
@RequiredArgsConstructor
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
        FinancialDataResponse response = financialDataService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all financial data", description = "Retrieve all financial data records with values converted to USD")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Financial data retrieved successfully")
    })
    public ResponseEntity<List<FinancialDataResponse>> findAll() {
        List<FinancialDataResponse> response = financialDataService.findAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{countryCode}")
    @Operation(summary = "Get financial data by country", description = "Retrieve financial data for a specific country with values converted to USD")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Financial data retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid country code"),
        @ApiResponse(responseCode = "404", description = "Financial data not found for this country")
    })
    public ResponseEntity<FinancialDataResponse> findByCountryCode(@PathVariable String countryCode) {
        FinancialDataResponse response = financialDataService.findByCountryCode(countryCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    @Operation(summary = "Get consolidated summary", description = "Retrieve consolidated summary of all financial data with totals in USD")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Summary retrieved successfully")
    })
    public ResponseEntity<ConsolidatedSummaryResponse> getSummary() {
        ConsolidatedSummaryResponse response = financialDataService.getSummary();
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
            @PathVariable String countryCode,
            @Valid @RequestBody FinancialDataRequest request) {
        FinancialDataResponse response = financialDataService.update(countryCode, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{countryCode}")
    @Operation(summary = "Delete financial data", description = "Delete financial data for a country")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Financial data deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid country code"),
        @ApiResponse(responseCode = "404", description = "Financial data not found for this country")
    })
    public ResponseEntity<Void> delete(@PathVariable String countryCode) {
        financialDataService.delete(countryCode);
        return ResponseEntity.noContent().build();
    }
}
