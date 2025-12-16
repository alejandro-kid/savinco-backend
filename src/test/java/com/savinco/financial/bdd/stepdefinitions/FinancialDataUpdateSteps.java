package com.savinco.financial.bdd.stepdefinitions;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.savinco.financial.bdd.support.ApiUrlBuilder;
import com.savinco.financial.bdd.support.TestContext;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class FinancialDataUpdateSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestContext testContext;

    @Autowired
    private ApiUrlBuilder urlBuilder;

    @LocalServerPort
    private int port;

    @When("I update financial data for country {string} with:")
    public void iUpdateFinancialDataForCountryWith(String countryCode, DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = rows.get(0);
        String currencyCode = data.get("currencyCode");

        // Determine the correct currency for each country
        String correctCurrency = countryCode.equals("ECU") ? "USD" : 
            (countryCode.equals("ESP") ? "EUR" : 
            (countryCode.equals("PER") ? "PEN" : "NPR"));
        
        // Ensure the correct currency for the country exists
        try {
            Map<String, Object> correctCurrencyRequestBody = new HashMap<>();
            correctCurrencyRequestBody.put("code", correctCurrency);
            correctCurrencyRequestBody.put("name", correctCurrency.equals("USD") ? "US Dollar" : 
                (correctCurrency.equals("EUR") ? "Euro" : 
                (correctCurrency.equals("PEN") ? "Peruvian Sol" : "Nepalese Rupee")));
            correctCurrencyRequestBody.put("isBase", correctCurrency.equals("USD"));
            correctCurrencyRequestBody.put("exchangeRateToBase", correctCurrency.equals("USD") ? java.math.BigDecimal.ONE : 
                (correctCurrency.equals("EUR") ? new java.math.BigDecimal("1.111111111111111111") :
                (correctCurrency.equals("PEN") ? new java.math.BigDecimal("0.303030302846212121") : new java.math.BigDecimal("0.0075"))));
            
            String currencyUrl = urlBuilder.buildCurrencyUrl(port);
            HttpEntity<Map<String, Object>> correctCurrencyRequest = new HttpEntity<>(correctCurrencyRequestBody);
            restTemplate.exchange(currencyUrl, HttpMethod.POST, correctCurrencyRequest, Map.class);
        } catch (Exception e) {
            // Currency might already exist, ignore
        }

        // Then, ensure the currency from the request exists (might be different from correct currency)
        if (!currencyCode.equals(correctCurrency)) {
            try {
                Map<String, Object> currencyRequestBody = new HashMap<>();
                currencyRequestBody.put("code", currencyCode);
                currencyRequestBody.put("name", currencyCode.equals("USD") ? "US Dollar" : 
                    (currencyCode.equals("EUR") ? "Euro" : 
                    (currencyCode.equals("PEN") ? "Peruvian Sol" : "Nepalese Rupee")));
                currencyRequestBody.put("isBase", currencyCode.equals("USD"));
                currencyRequestBody.put("exchangeRateToBase", currencyCode.equals("USD") ? java.math.BigDecimal.ONE : 
                    (currencyCode.equals("EUR") ? new java.math.BigDecimal("1.111111111111111111") :
                    (currencyCode.equals("PEN") ? new java.math.BigDecimal("0.303030302846212121") : new java.math.BigDecimal("0.0075"))));
                
                String currencyUrl = urlBuilder.buildCurrencyUrl(port);
                HttpEntity<Map<String, Object>> currencyRequest = new HttpEntity<>(currencyRequestBody);
                restTemplate.exchange(currencyUrl, HttpMethod.POST, currencyRequest, Map.class);
            } catch (Exception e) {
                // Currency might already exist, ignore
            }
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("countryCode", countryCode);
        requestBody.put("currencyCode", currencyCode);
        requestBody.put("capitalSaved", new java.math.BigDecimal(data.get("capitalSaved")));
        requestBody.put("capitalLoaned", new java.math.BigDecimal(data.get("capitalLoaned")));
        requestBody.put("profitsGenerated", new java.math.BigDecimal(data.get("profitsGenerated")));

        String url = urlBuilder.buildFinancialDataUrl(port, countryCode);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            url,
            HttpMethod.PUT,
            request,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        testContext.setLastResponse(response);
    }

    @When("I update financial data for country {string} with body containing country code {string}:")
    public void iUpdateFinancialDataForCountryWithBodyContainingCountryCode(String pathCountryCode, String bodyCountryCode, DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = rows.get(0);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("countryCode", bodyCountryCode);
        requestBody.put("currencyCode", data.get("currencyCode"));
        requestBody.put("capitalSaved", new java.math.BigDecimal(data.get("capitalSaved")));
        requestBody.put("capitalLoaned", new java.math.BigDecimal(data.get("capitalLoaned")));
        requestBody.put("profitsGenerated", new java.math.BigDecimal(data.get("profitsGenerated")));

        String url = urlBuilder.buildFinancialDataUrl(port, pathCountryCode);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            url,
            HttpMethod.PUT,
            request,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        testContext.setLastResponse(response);
    }


    @Then("the response should contain error message about country code mismatch")
    public void theResponseShouldContainErrorMessageAboutCountryCodeMismatch() {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        Object message = body.get("message");
        assertNotNull(message, "Error message should not be null");
        String messageStr = message.toString().toLowerCase();
        assertTrue(
            messageStr.contains("mismatch") || messageStr.contains("match") || messageStr.contains("different") || messageStr.contains("path"),
            "Error message should mention country code mismatch: " + message
        );
    }

}