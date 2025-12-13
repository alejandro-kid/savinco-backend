package com.savinco.financial.bdd.stepdefinitions;

import com.savinco.financial.bdd.support.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HealthCheckSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestContext testContext;

    @LocalServerPort
    private int port;

    @Given("the API is running")
    public void theApiIsRunning() {
        // API is running by default in Spring Boot test context
        assertNotNull(restTemplate);
    }

    @When("I request the health check endpoint")
    public void iRequestTheHealthCheckEndpoint() {
        String url = "http://localhost:" + port + "/api/v1/health";
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            url,
            org.springframework.http.HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        testContext.setLastResponse(response);
    }

    @Then("I should receive status code {int} immediately")
    public void iShouldReceiveStatusCodeImmediately(int expectedStatusCode) {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        assertEquals(
            HttpStatus.valueOf(expectedStatusCode),
            testContext.getLastResponse().getStatusCode(),
            "Expected status code " + expectedStatusCode
        );
    }

    @Then("the response should contain status {string}")
    public void theResponseShouldContainStatus(String expectedStatus) {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        assertNotNull(testContext.getLastResponse().getBody(), "Response body should not be null");
        assertEquals(
            expectedStatus,
            testContext.getLastResponse().getBody().get("status"),
            "Expected status to be " + expectedStatus
        );
    }

    @Then("the response should contain timestamp")
    public void theResponseShouldContainTimestamp() {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        assertNotNull(testContext.getLastResponse().getBody(), "Response body should not be null");
        assertNotNull(
            testContext.getLastResponse().getBody().get("timestamp"),
            "Response should contain timestamp"
        );
    }
}
