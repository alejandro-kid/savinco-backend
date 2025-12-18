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

public class CountryDeleteSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestContext testContext;

    @Autowired
    private ApiUrlBuilder urlBuilder;

    @LocalServerPort
    private int port;

    @When("I delete country with code {string}")
    public void iDeleteCountryWithCode(String code) {
        String url = urlBuilder.buildCountryUrl(port, code);
        // Use ParameterizedTypeReference to handle both success (204) and error (4xx) responses
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            url,
            HttpMethod.DELETE,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        testContext.setLastResponse(response);
    }

    @Then("the response should contain error message about country having associated financial data")
    public void theResponseShouldContainErrorMessageAboutCountryHavingAssociatedFinancialData() {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = testContext.getLastResponseBodyAsMap();
        assertNotNull(body, "Response body should not be null");
        Object message = body.get("message");
        assertNotNull(message, "Error message should not be null");
        String messageStr = message.toString().toLowerCase();
        assertTrue(
            messageStr.contains("financial data") || messageStr.contains("associated") || messageStr.contains("cannot delete"),
            "Error message should mention country having associated financial data: " + message
        );
    }

}
