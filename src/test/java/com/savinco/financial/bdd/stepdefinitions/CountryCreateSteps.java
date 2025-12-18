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
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CountryCreateSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestContext testContext;

    @Autowired
    private ApiUrlBuilder urlBuilder;

    @LocalServerPort
    private int port;

    @Given("currency exists with code {string}")
    public void currencyExistsWithCode(String code) {
        // This will be implemented when we have the repository/service
        // For now, we'll create the currency via API
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("code", code);
        requestBody.put("name", code.equals("USD") ? "US Dollar" : "Euro");
        requestBody.put("isBase", code.equals("USD"));
        requestBody.put("exchangeRateToBase", code.equals("USD") ? java.math.BigDecimal.ONE : new java.math.BigDecimal("0.90"));

        String url = urlBuilder.buildCurrencyUrl(port);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody);
        restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
    }

    @Given("no country exists with code {string}")
    public void noCountryExistsWithCode(String code) {
        // This will be implemented when we have the repository/service
        // For now, we assume clean state
    }

    @Given("country exists with code {string} and name {string}")
    public void countryExistsWithCodeAndName(String code, String name) {
        // This will be implemented when we have the repository/service
        // For now, we'll create the data via API
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("code", code);
        requestBody.put("name", name);
        requestBody.put("currencyCode", "USD");

        String url = urlBuilder.buildCountryUrl(port);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody);
        restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
    }

    @Given("country exists with code {string} and name {string} and currencyCode {string}")
    public void countryExistsWithCodeAndNameAndCurrencyCode(String code, String name, String currencyCode) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("code", code);
        requestBody.put("name", name);
        requestBody.put("currencyCode", currencyCode);

        String url = urlBuilder.buildCountryUrl(port);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody);
        restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
    }

    @Given("country exists with code {string} and currencyCode {string}")
    public void countryExistsWithCodeAndCurrencyCode(String code, String currencyCode) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("code", code);
        requestBody.put("name", code.equals("ECU") ? "Ecuador" : (code.equals("ESP") ? "España" : "Country"));
        requestBody.put("currencyCode", currencyCode);

        String url = urlBuilder.buildCountryUrl(port);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody);
        try {
            restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
        } catch (Exception e) {
            // Country might already exist, ignore
        }
    }

    @When("I create country with:")
    public void iCreateCountryWith(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = rows.get(0);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("code", data.get("code"));
        requestBody.put("name", data.get("name"));
        requestBody.put("currencyCode", data.get("currencyCode"));

        String url = urlBuilder.buildCountryUrl(port);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        testContext.setLastResponse(response);
    }

    @Then("the response should contain error message about duplicate country code")
    public void theResponseShouldContainErrorMessageAboutDuplicateCountryCode() {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        Object message = body.get("message");
        assertNotNull(message, "Error message should not be null");
        String messageStr = message.toString().toLowerCase();
        assertTrue(
            messageStr.contains("duplicate") || messageStr.contains("already exists") || messageStr.contains("unique"),
            "Error message should mention duplicate country code: " + message
        );
    }

    @Then("the response should contain error message about invalid country code format")
    public void theResponseShouldContainErrorMessageAboutInvalidCountryCodeFormat() {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        Object message = body.get("message");
        assertNotNull(message, "Error message should not be null");
        String messageStr = message.toString().toLowerCase();
        assertTrue(
            messageStr.contains("country") || messageStr.contains("invalid") || messageStr.contains("format"),
            "Error message should mention invalid country code format: " + message
        );
    }

}

