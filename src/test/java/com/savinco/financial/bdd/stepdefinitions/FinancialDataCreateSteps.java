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

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("countryCode", countryCode);
        requestBody.put("currencyCode", data.get("currencyCode"));
        requestBody.put("capitalSaved", new BigDecimal(data.get("capitalSaved")));
        requestBody.put("capitalLoaned", new BigDecimal(data.get("capitalLoaned")));
        requestBody.put("profitsGenerated", new BigDecimal(data.get("profitsGenerated")));

        String url = urlBuilder.buildFinancialDataUrl(port);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody);
        restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
    }

    @When("I create financial data with:")
    public void iCreateFinancialDataWith(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = rows.get(0);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("countryCode", data.get("countryCode"));
        requestBody.put("currencyCode", data.get("currencyCode"));
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

    @Then("the response should contain country code {string}")
    public void theResponseShouldContainCountryCode(String expectedCountryCode) {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        assertEquals(
            expectedCountryCode,
            body.get("countryCode"),
            "Expected country code to be " + expectedCountryCode
        );
    }

    @Then("the response should contain country name {string}")
    public void theResponseShouldContainCountryName(String expectedCountryName) {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        assertEquals(
            expectedCountryName,
            body.get("countryName"),
            "Expected country name to be " + expectedCountryName
        );
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
        
        assertEquals(0, expected.compareTo(actual), 
            "Expected capital saved to be " + expectedValue + " but was " + actual);
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
        
        assertEquals(0, expected.compareTo(actual), 
            "Expected capital loaned to be " + expectedValue + " but was " + actual);
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
        
        assertEquals(0, expected.compareTo(actual), 
            "Expected profits generated to be " + expectedValue + " but was " + actual);
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
        
        assertEquals(0, expected.compareTo(actual), 
            "Expected total in USD to be " + expectedValue + " but was " + actual);
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

    @Then("the response should contain error message about country not found")
    public void theResponseShouldContainErrorMessageAboutCountryNotFound() {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        Object message = body.get("message");
        assertNotNull(message, "Error message should not be null");
        String messageStr = message.toString().toLowerCase();
        assertTrue(
            messageStr.contains("not found") || messageStr.contains("does not exist") || messageStr.contains("country"),
            "Error message should mention country not found: " + message
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
