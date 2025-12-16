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

public class CurrencyGetSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestContext testContext;

    @Autowired
    private ApiUrlBuilder urlBuilder;

    @LocalServerPort
    private int port;

    @When("I get currency by code {string}")
    public void iGetCurrencyByCode(String code) {
        String url = urlBuilder.buildCurrencyUrl(port, code);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        testContext.setLastResponse(response);
    }

    @When("I list all currencies")
    public void iListAllCurrencies() {
        String url = urlBuilder.buildCurrencyUrl(port);
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        testContext.setLastResponse(response);
    }

    @When("I get base currency")
    public void iGetBaseCurrency() {
        String url = urlBuilder.buildCurrencyBaseUrl(port);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        testContext.setLastResponse(response);
    }

    @Then("the response should contain {int} currencies")
    public void theResponseShouldContainCurrencies(int expectedCount) {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> body = (List<Map<String, Object>>) testContext.getLastResponse().getBody();
        assertNotNull(body, "Response body should not be null");
        assertEquals(
            expectedCount,
            body.size(),
            "Expected " + expectedCount + " currencies but found " + body.size()
        );
    }

    @Then("the response should contain currency with code {string}")
    public void theResponseShouldContainCurrencyWithCode(String expectedCode) {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> body = (List<Map<String, Object>>) testContext.getLastResponse().getBody();
        assertNotNull(body, "Response body should not be null");
        boolean found = body.stream()
            .anyMatch(currency -> expectedCode.equals(currency.get("code")));
        assertTrue(found, "Expected to find currency with code " + expectedCode);
    }

    @Then("the response should contain error message about currency not found")
    public void theResponseShouldContainErrorMessageAboutCurrencyNotFound() {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        Object message = body.get("message");
        assertNotNull(message, "Error message should not be null");
        String messageStr = message.toString().toLowerCase();
        assertTrue(
            messageStr.contains("not found") || messageStr.contains("does not exist") || messageStr.contains("currency"),
            "Error message should mention currency not found: " + message
        );
    }
}

