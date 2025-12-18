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

public class CurrencyCreateSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestContext testContext;

    @Autowired
    private ApiUrlBuilder urlBuilder;

    @LocalServerPort
    private int port;

    @Given("no currency exists with code {string}")
    public void noCurrencyExistsWithCode(String code) {
        // This will be implemented when we have the repository/service
        // For now, we assume clean state
    }

    @Given("currency exists with code {string} and name {string}")
    public void currencyExistsWithCodeAndName(String code, String name) {
        // This will be implemented when we have the repository/service
        // For now, we'll create the data via API
        // The service will automatically determine if this is the base currency
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("code", code);
        requestBody.put("name", name);
        // Don't send isBase - let the service determine it automatically
        // If it's the first currency, it becomes base with rate 1.00
        // If not, it uses the provided rate
        requestBody.put("exchangeRateToBase", code.equals("USD") ? BigDecimal.ONE : new BigDecimal("0.90"));

        String url = urlBuilder.buildCurrencyUrl(port);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody);
        restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
    }

    @Given("currency exists with code {string} and isBase {string}")
    public void currencyExistsWithCodeAndIsBase(String code, String isBaseStr) {
        boolean isBase = Boolean.parseBoolean(isBaseStr);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("code", code);
        requestBody.put("name", code.equals("USD") ? "US Dollar" : "Euro");
        // Only send isBase if explicitly true (for first currency)
        // The service will reject if isBase=true and a base currency already exists
        if (isBase) {
            requestBody.put("isBase", true);
        }
        // If it's the first currency, it will become base with rate 1.00 automatically
        // If not, use the provided rate
        requestBody.put("exchangeRateToBase", isBase ? BigDecimal.ONE : new BigDecimal("0.90"));

        String url = urlBuilder.buildCurrencyUrl(port);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody);
        restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
    }

    @Given("currency exists with code {string} and exchangeRateToBase {string}")
    public void currencyExistsWithCodeAndExchangeRateToBase(String code, String rate) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("code", code);
        requestBody.put("name", code.equals("USD") ? "US Dollar" : "Euro");
        // Don't send isBase - let the service determine it automatically
        // If it's the first currency, it becomes base with rate 1.00 (ignoring provided rate)
        // If not, it uses the provided rate
        requestBody.put("exchangeRateToBase", new BigDecimal(rate));

        String url = urlBuilder.buildCurrencyUrl(port);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody);
        restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
    }

    @When("I create currency with:")
    public void iCreateCurrencyWith(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = rows.get(0);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("code", data.get("code"));
        requestBody.put("name", data.get("name"));
        requestBody.put("isBase", Boolean.parseBoolean(data.get("isBase")));
        requestBody.put("exchangeRateToBase", new BigDecimal(data.get("exchangeRateToBase")));

        String url = urlBuilder.buildCurrencyUrl(port);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        testContext.setLastResponse(response);
    }

    @Then("the response should contain currency code {string}")
    public void theResponseShouldContainCurrencyCode(String expectedCode) {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        Object actualCode = body.containsKey("currencyCode") ? body.get("currencyCode") : body.get("code");
        assertNotNull(actualCode, "Currency code should not be null");
        assertEquals(
            expectedCode,
            actualCode,
            "Expected currency code to be " + expectedCode
        );
    }

    @Then("the response should contain currency name {string}")
    public void theResponseShouldContainCurrencyName(String expectedName) {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        assertEquals(
            expectedName,
            body.get("name"),
            "Expected currency name to be " + expectedName
        );
    }

    @Then("the response should contain isBase {string}")
    public void theResponseShouldContainIsBase(String expectedIsBase) {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        Boolean expected = Boolean.parseBoolean(expectedIsBase);
        Object actual = body.get("isBase");
        assertEquals(
            expected,
            actual instanceof Boolean ? (Boolean) actual : Boolean.parseBoolean(actual.toString()),
            "Expected isBase to be " + expectedIsBase
        );
    }

    @Then("the response should contain exchangeRateToBase {string}")
    public void theResponseShouldContainExchangeRateToBase(String expectedRate) {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        Object exchangeRate = body.get("exchangeRateToBase");
        assertNotNull(exchangeRate, "exchangeRateToBase should not be null");
        
        BigDecimal expected = new BigDecimal(expectedRate);
        BigDecimal actual = exchangeRate instanceof BigDecimal 
            ? (BigDecimal) exchangeRate 
            : new BigDecimal(exchangeRate.toString());
        
        assertEquals(0, expected.compareTo(actual), 
            "Expected exchangeRateToBase to be " + expectedRate + " but was " + actual);
    }

    @Then("the response should contain error message about duplicate currency code")
    public void theResponseShouldContainErrorMessageAboutDuplicateCurrencyCode() {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        Object message = body.get("message");
        assertNotNull(message, "Error message should not be null");
        String messageStr = message.toString().toLowerCase();
        assertTrue(
            messageStr.contains("duplicate") || messageStr.contains("already exists") || messageStr.contains("unique"),
            "Error message should mention duplicate currency code: " + message
        );
    }

    @Then("the response should contain error message about invalid currency code format")
    public void theResponseShouldContainErrorMessageAboutInvalidCurrencyCodeFormat() {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        Object message = body.get("message");
        assertNotNull(message, "Error message should not be null");
        String messageStr = message.toString().toLowerCase();
        assertTrue(
            messageStr.contains("currency") || messageStr.contains("invalid") || messageStr.contains("format"),
            "Error message should mention invalid currency code format: " + message
        );
    }

    @Then("the response should contain error message about base currency already exists")
    public void theResponseShouldContainErrorMessageAboutBaseCurrencyAlreadyExists() {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        Object message = body.get("message");
        assertNotNull(message, "Error message should not be null");
        String messageStr = message.toString().toLowerCase();
        assertTrue(
            messageStr.contains("base") || messageStr.contains("already") || messageStr.contains("exists"),
            "Error message should mention base currency already exists: " + message
        );
    }

    @Then("the response should contain error message about negative exchange rate")
    public void theResponseShouldContainErrorMessageAboutNegativeExchangeRate() {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        Object message = body.get("message");
        assertNotNull(message, "Error message should not be null");
        String messageStr = message.toString().toLowerCase();
        assertTrue(
            messageStr.contains("negative") || messageStr.contains("positive") || messageStr.contains("greater") || messageStr.contains("minimum"),
            "Error message should mention negative exchange rate: " + message
        );
    }

    @Then("the response should contain error message about invalid exchange rate")
    public void theResponseShouldContainErrorMessageAboutInvalidExchangeRate() {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        Object message = body.get("message");
        assertNotNull(message, "Error message should not be null");
        String messageStr = message.toString().toLowerCase();
        assertTrue(
            messageStr.contains("exchange") || messageStr.contains("rate") || messageStr.contains("invalid") || messageStr.contains("zero"),
            "Error message should mention invalid exchange rate: " + message
        );
    }

    @Then("the response should contain error message about missing required fields")
    public void theResponseShouldContainErrorMessageAboutMissingRequiredFields() {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        Object message = body.get("message");
        assertNotNull(message, "Error message should not be null");
        String messageStr = message.toString().toLowerCase();
        assertTrue(
            messageStr.contains("required") || messageStr.contains("missing") || messageStr.contains("null") || messageStr.contains("empty"),
            "Error message should mention missing required fields: " + message
        );
    }
}

