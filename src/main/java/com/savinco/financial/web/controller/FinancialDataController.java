package com.savinco.financial.web.controller;

import com.savinco.financial.application.dto.FinancialDataRequest;
import com.savinco.financial.application.dto.FinancialDataResponse;
import com.savinco.financial.application.service.FinancialDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
