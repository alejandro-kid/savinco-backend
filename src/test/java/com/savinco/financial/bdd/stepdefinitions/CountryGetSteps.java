package com.savinco.financial.bdd.stepdefinitions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.savinco.financial.bdd.support.ApiUrlBuilder;
import com.savinco.financial.bdd.support.TestContext;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CountryGetSteps {

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
        // For now, we assume data exists
    }

    @Given("country exists with code {string} and name {string} and currencyCode {string}")
    public void countryExistsWithCodeAndNameAndCurrencyCode(String code, String name, String currencyCode) {
        // This will be implemented when we have the repository/service
        // For now, we assume data exists
    }

    @When("I get country by code {string}")
    public void iGetCountryByCode(String code) {
        String url = urlBuilder.buildCountryUrl(port, code);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        testContext.setLastResponse(response);
    }

    @When("I list all countries")
    public void iListAllCountries() {
        String url = urlBuilder.buildCountryUrl(port);
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        testContext.setLastResponse(response);
    }

    @Then("the response should contain {int} countries")
    public void theResponseShouldContainCountries(int expectedCount) {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> body = (List<Map<String, Object>>) testContext.getLastResponse().getBody();
        assertNotNull(body, "Response body should not be null");
        assertEquals(
            expectedCount,
            body.size(),
            "Expected " + expectedCount + " countries but found " + body.size()
        );
    }

    @Then("the response should contain country with code {string}")
    public void theResponseShouldContainCountryWithCode(String expectedCode) {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> body = (List<Map<String, Object>>) testContext.getLastResponse().getBody();
        assertNotNull(body, "Response body should not be null");
        boolean found = body.stream()
            .anyMatch(country -> expectedCode.equals(country.get("code")));
        assertTrue(found, "Expected to find country with code " + expectedCode);
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
}

