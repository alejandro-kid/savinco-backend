package com.savinco.financial.bdd.stepdefinitions;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.savinco.financial.bdd.support.ApiUrlBuilder;
import com.savinco.financial.bdd.support.TestContext;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class FinancialDataDeleteSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestContext testContext;

    @Autowired
    private ApiUrlBuilder urlBuilder;

    @LocalServerPort
    private int port;

    @When("I delete financial data for country {string}")
    public void iDeleteFinancialDataForCountry(String countryCode) {
        String url = urlBuilder.buildFinancialDataUrl(port, countryCode);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            url,
            HttpMethod.DELETE,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        testContext.setLastResponse(response);
    }

    @Then("the response body should be empty")
    public void theResponseBodyShouldBeEmpty() {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Object body = testContext.getLastResponse().getBody();
        assertTrue(
            body == null || (body instanceof String && ((String) body).isEmpty()),
            "Response body should be empty"
        );
    }

}