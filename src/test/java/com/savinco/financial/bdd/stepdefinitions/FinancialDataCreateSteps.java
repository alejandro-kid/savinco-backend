package com.savinco.financial.bdd.stepdefinitions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
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
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class FinancialDataCreateSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestContext testContext;

    @Autowired
    private ApiUrlBuilder urlBuilder;

    @LocalServerPort
    private int port;

    @Given("no financial data exists for country {string}")
    public void noFinancialDataExistsForCountry(String countryCode) {
        // This will be implemented when we have the repository/service
        // For now, we assume clean state
    }

    @Given("financial data exists for country {string} with:")
    public void financialDataExistsForCountryWith(String countryCode, DataTable dataTable) {
        // This will be implemented when we have the repository/service
        // For now, we'll create the data via API
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = rows.get(0);
        String currencyCode = data.get("currencyCode");

        // First, ensure currency exists
        try {
            Map<String, Object> currencyRequestBody = new HashMap<>();
            currencyRequestBody.put("code", currencyCode);
            currencyRequestBody.put("name", currencyCode.equals("USD") ? "US Dollar" : 
                (currencyCode.equals("EUR") ? "Euro" : 
                (currencyCode.equals("PEN") ? "Peruvian Sol" : "Nepalese Rupee")));
            currencyRequestBody.put("isBase", currencyCode.equals("USD"));
            currencyRequestBody.put("exchangeRateToBase", currencyCode.equals("USD") ? BigDecimal.ONE : 
                (currencyCode.equals("EUR") ? new BigDecimal("1.111111111111111111") :
                (currencyCode.equals("PEN") ? new BigDecimal("0.303030302846212121") : new BigDecimal("0.0075"))));
            
            String currencyUrl = urlBuilder.buildCurrencyUrl(port);
            HttpEntity<Map<String, Object>> currencyRequest = new HttpEntity<>(currencyRequestBody);
            restTemplate.exchange(currencyUrl, HttpMethod.POST, currencyRequest, Map.class);
        } catch (Exception e) {
            // Currency might already exist, ignore
        }

        // Then, ensure country exists with the correct currency for that country
        try {
            // Determine the correct currency for each country
            String correctCurrency = countryCode.equals("ECU") ? "USD" : 
                (countryCode.equals("ESP") ? "EUR" : 
                (countryCode.equals("PER") ? "PEN" : "NPR"));
            
            Map<String, Object> countryRequestBody = new HashMap<>();
            countryRequestBody.put("code", countryCode);
            countryRequestBody.put("name", countryCode.equals("ECU") ? "Ecuador" : 
                (countryCode.equals("ESP") ? "España" : 
                (countryCode.equals("PER") ? "Perú" : "Nepal")));
            countryRequestBody.put("currencyCode", correctCurrency);
            
            String countryUrl = urlBuilder.buildCountryUrl(port);
            HttpEntity<Map<String, Object>> countryRequest = new HttpEntity<>(countryRequestBody);
            restTemplate.exchange(countryUrl, HttpMethod.POST, countryRequest, Map.class);
        } catch (Exception e) {
            // Country might already exist, ignore
        }

        // Finally, create financial data
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("countryCode", countryCode);
        requestBody.put("currencyCode", currencyCode);
        requestBody.put("capitalSaved", new BigDecimal(data.get("capitalSaved")));
        requestBody.put("capitalLoaned", new BigDecimal(data.get("capitalLoaned")));
        requestBody.put("profitsGenerated", new BigDecimal(data.get("profitsGenerated")));

        String url = urlBuilder.buildFinancialDataUrl(port);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody);
        try {
            restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
        } catch (Exception e) {
            // Financial data might already exist, ignore
        }
    }

    @When("I create financial data with:")
    public void iCreateFinancialDataWith(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = rows.get(0);
        String countryCode = data.get("countryCode");
        String currencyCode = data.get("currencyCode");

        // Determine the correct currency for each country
        String correctCurrency = countryCode.equals("ECU") ? "USD" : 
            (countryCode.equals("ESP") ? "EUR" : 
            (countryCode.equals("PER") ? "PEN" : "NPR"));
        
        // First, ensure the correct currency for the country exists
        try {
            Map<String, Object> correctCurrencyRequestBody = new HashMap<>();
            correctCurrencyRequestBody.put("code", correctCurrency);
            correctCurrencyRequestBody.put("name", correctCurrency.equals("USD") ? "US Dollar" : 
                (correctCurrency.equals("EUR") ? "Euro" : 
                (correctCurrency.equals("PEN") ? "Peruvian Sol" : "Nepalese Rupee")));
            // Don't send isBase - let the service determine it automatically
            // If it's the first currency, it becomes base with rate 1.00
            // If not, use the provided rate
            correctCurrencyRequestBody.put("exchangeRateToBase", correctCurrency.equals("USD") ? BigDecimal.ONE : 
                (correctCurrency.equals("EUR") ? new BigDecimal("1.111111111111111111") :
                (correctCurrency.equals("PEN") ? new BigDecimal("0.303030301557575744") : new BigDecimal("0.0075"))));
            
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
                // Don't send isBase - let the service determine it automatically
                // If it's the first currency, it becomes base with rate 1.00
                // If not, use the provided rate
                currencyRequestBody.put("exchangeRateToBase", currencyCode.equals("USD") ? BigDecimal.ONE : 
                    (currencyCode.equals("EUR") ? new BigDecimal("1.111111111111111111") :
                    (currencyCode.equals("PEN") ? new BigDecimal("0.303030302846212121") : new BigDecimal("0.0075"))));
                
                String currencyUrl = urlBuilder.buildCurrencyUrl(port);
                HttpEntity<Map<String, Object>> currencyRequest = new HttpEntity<>(currencyRequestBody);
                restTemplate.exchange(currencyUrl, HttpMethod.POST, currencyRequest, Map.class);
            } catch (Exception e) {
                // Currency might already exist, ignore
            }
        }

        // Then, ensure country exists with the correct currency for that country
        try {
            Map<String, Object> countryRequestBody = new HashMap<>();
            countryRequestBody.put("code", countryCode);
            countryRequestBody.put("name", countryCode.equals("ECU") ? "Ecuador" : 
                (countryCode.equals("ESP") ? "España" : 
                (countryCode.equals("PER") ? "Perú" : "Nepal")));
            countryRequestBody.put("currencyCode", correctCurrency);
            
            String countryUrl = urlBuilder.buildCountryUrl(port);
            HttpEntity<Map<String, Object>> countryRequest = new HttpEntity<>(countryRequestBody);
            restTemplate.exchange(countryUrl, HttpMethod.POST, countryRequest, Map.class);
        } catch (Exception e) {
            // Country might already exist, ignore
        }

        // Finally, create financial data
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("countryCode", countryCode);
        requestBody.put("currencyCode", currencyCode);
        requestBody.put("capitalSaved", new BigDecimal(data.get("capitalSaved")));
        requestBody.put("capitalLoaned", new BigDecimal(data.get("capitalLoaned")));
        requestBody.put("profitsGenerated", new BigDecimal(data.get("profitsGenerated")));

        String url = urlBuilder.buildFinancialDataUrl(port);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        testContext.setLastResponse(response);
    }

    @Then("the response should contain original currency {string}")
    public void theResponseShouldContainOriginalCurrency(String expectedCurrency) {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        assertEquals(
            expectedCurrency,
            body.get("originalCurrency"),
            "Expected original currency to be " + expectedCurrency
        );
    }

    @Then("the response should contain capital saved in USD {string}")
    public void theResponseShouldContainCapitalSavedInUSD(String expectedValue) {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        Object capitalSaved = body.get("capitalSaved");
        assertNotNull(capitalSaved, "capitalSaved should not be null");
        
        BigDecimal expected = new BigDecimal(expectedValue);
        BigDecimal actual = capitalSaved instanceof BigDecimal 
            ? (BigDecimal) capitalSaved 
            : new BigDecimal(capitalSaved.toString());
        
        // Use delta comparison for monetary values to account for rounding differences
        BigDecimal delta = new BigDecimal("0.01");
        BigDecimal difference = expected.subtract(actual).abs();
        assertTrue(difference.compareTo(delta) <= 0,
            "Expected capital saved to be " + expectedValue + " (±0.01) but was " + actual);
    }

    @Then("the response should contain capital loaned in USD {string}")
    public void theResponseShouldContainCapitalLoanedInUSD(String expectedValue) {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        Object capitalLoaned = body.get("capitalLoaned");
        assertNotNull(capitalLoaned, "capitalLoaned should not be null");
        
        BigDecimal expected = new BigDecimal(expectedValue);
        BigDecimal actual = capitalLoaned instanceof BigDecimal 
            ? (BigDecimal) capitalLoaned 
            : new BigDecimal(capitalLoaned.toString());
        
        // Use delta comparison for monetary values to account for rounding differences
        BigDecimal delta = new BigDecimal("0.01");
        BigDecimal difference = expected.subtract(actual).abs();
        assertTrue(difference.compareTo(delta) <= 0,
            "Expected capital loaned to be " + expectedValue + " (±0.01) but was " + actual);
    }

    @Then("the response should contain profits generated in USD {string}")
    public void theResponseShouldContainProfitsGeneratedInUSD(String expectedValue) {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        Object profitsGenerated = body.get("profitsGenerated");
        assertNotNull(profitsGenerated, "profitsGenerated should not be null");
        
        BigDecimal expected = new BigDecimal(expectedValue);
        BigDecimal actual = profitsGenerated instanceof BigDecimal 
            ? (BigDecimal) profitsGenerated 
            : new BigDecimal(profitsGenerated.toString());
        
        // Use delta comparison for monetary values to account for rounding differences
        BigDecimal delta = new BigDecimal("0.01");
        BigDecimal difference = expected.subtract(actual).abs();
        assertTrue(difference.compareTo(delta) <= 0,
            "Expected profits generated to be " + expectedValue + " (±0.01) but was " + actual);
    }

    @Then("the response should contain total in USD {string}")
    public void theResponseShouldContainTotalInUSD(String expectedValue) {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        Object totalInUSD = body.get("totalInUSD");
        assertNotNull(totalInUSD, "totalInUSD should not be null");
        
        BigDecimal expected = new BigDecimal(expectedValue);
        BigDecimal actual = totalInUSD instanceof BigDecimal 
            ? (BigDecimal) totalInUSD 
            : new BigDecimal(totalInUSD.toString());
        
        // Use delta comparison for monetary values to account for rounding differences
        BigDecimal delta = new BigDecimal("0.01");
        BigDecimal difference = expected.subtract(actual).abs();
        assertTrue(difference.compareTo(delta) <= 0,
            "Expected total in USD to be " + expectedValue + " (±0.01) but was " + actual);
    }

    @Then("the response should contain error message about duplicate country")
    public void theResponseShouldContainErrorMessageAboutDuplicateCountry() {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        Object message = body.get("message");
        assertNotNull(message, "Error message should not be null");
        String messageStr = message.toString().toLowerCase();
        assertTrue(
            messageStr.contains("duplicate") || messageStr.contains("already exists") || messageStr.contains("unique"),
            "Error message should mention duplicate country: " + message
        );
    }

    @Then("the response should contain error message about invalid country code")
    public void theResponseShouldContainErrorMessageAboutInvalidCountryCode() {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        Object message = body.get("message");
        assertNotNull(message, "Error message should not be null");
        String messageStr = message.toString().toLowerCase();
        assertTrue(
            messageStr.contains("country") || messageStr.contains("invalid"),
            "Error message should mention invalid country code: " + message
        );
    }

    @Then("the response should contain error message about currency mismatch")
    public void theResponseShouldContainErrorMessageAboutCurrencyMismatch() {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        Object message = body.get("message");
        assertNotNull(message, "Error message should not be null");
        String messageStr = message.toString().toLowerCase();
        assertTrue(
            messageStr.contains("currency") || messageStr.contains("match") || messageStr.contains("mismatch"),
            "Error message should mention currency mismatch: " + message
        );
    }

    @Then("the response should contain error message about negative values")
    public void theResponseShouldContainErrorMessageAboutNegativeValues() {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        Object message = body.get("message");
        assertNotNull(message, "Error message should not be null");
        String messageStr = message.toString().toLowerCase();
        assertTrue(
            messageStr.contains("negative") || messageStr.contains("positive") || messageStr.contains("greater") || messageStr.contains("minimum"),
            "Error message should mention negative values: " + message
        );
    }

    @Then("the response should contain error message about invalid currency code")
    public void theResponseShouldContainErrorMessageAboutInvalidCurrencyCode() {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        Object message = body.get("message");
        assertNotNull(message, "Error message should not be null");
        String messageStr = message.toString().toLowerCase();
        assertTrue(
            messageStr.contains("currency") || messageStr.contains("invalid"),
            "Error message should mention invalid currency code: " + message
        );
    }
}
