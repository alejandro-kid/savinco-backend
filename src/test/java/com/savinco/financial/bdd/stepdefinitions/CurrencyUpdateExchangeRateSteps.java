package com.savinco.financial.bdd.stepdefinitions;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.HashMap;
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

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CurrencyUpdateExchangeRateSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestContext testContext;

    @Autowired
    private ApiUrlBuilder urlBuilder;

    @LocalServerPort
    private int port;

    @When("I update exchange rate for currency {string} to {string}")
    public void iUpdateExchangeRateForCurrencyTo(String code, String newRate) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("exchangeRateToBase", new BigDecimal(newRate));

        String url = urlBuilder.buildCurrencyExchangeRateUrl(port, code);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            url,
            HttpMethod.PUT,
            request,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        testContext.setLastResponse(response);
    }

    @Then("the response should contain error message about cannot update base currency rate")
    public void theResponseShouldContainErrorMessageAboutCannotUpdateBaseCurrencyRate() {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        Object message = body.get("message");
        assertNotNull(message, "Error message should not be null");
        String messageStr = message.toString().toLowerCase();
        assertTrue(
            messageStr.contains("base") || messageStr.contains("cannot") || messageStr.contains("update"),
            "Error message should mention cannot update base currency rate: " + message
        );
    }
}

