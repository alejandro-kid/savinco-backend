package com.savinco.financial.bdd.stepdefinitions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
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

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class FinancialDataGetSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestContext testContext;

    @Autowired
    private ApiUrlBuilder urlBuilder;

    @LocalServerPort
    private int port;

    @When("I request all financial data")
    public void iRequestAllFinancialData() {
        String url = urlBuilder.buildFinancialDataUrl(port);
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        testContext.setLastResponse(response);
    }

    @When("I request financial data for country {string}")
    public void iRequestFinancialDataForCountry(String countryCode) {
        String url = urlBuilder.buildFinancialDataUrl(port, countryCode);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        testContext.setLastResponse(response);
    }

    @When("I request financial data summary")
    public void iRequestFinancialDataSummary() {
        String url = urlBuilder.buildFinancialDataSummaryUrl(port);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        testContext.setLastResponse(response);
    }

    @Then("the response should be an empty list")
    public void theResponseShouldBeAnEmptyList() {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        List<?> body = (List<?>) testContext.getLastResponse().getBody();
        assertNotNull(body, "Response body should not be null");
        assertTrue(body.isEmpty(), "Response should be an empty list");
    }

    @Then("the response should be a list with {int} items")
    public void theResponseShouldBeAListWithItems(int expectedCount) {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        List<?> body = (List<?>) testContext.getLastResponse().getBody();
        assertNotNull(body, "Response body should not be null");
        assertEquals(expectedCount, body.size(), 
            "Expected list to have " + expectedCount + " items");
    }

    @Then("the list should contain country code {string}")
    @SuppressWarnings("unchecked")
    public void theListShouldContainCountryCode(String expectedCountryCode) {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        List<Map<String, Object>> body = (List<Map<String, Object>>) testContext.getLastResponse().getBody();
        assertNotNull(body, "Response body should not be null");
        
        boolean found = body.stream()
            .anyMatch(item -> expectedCountryCode.equals(item.get("countryCode")));
        
        assertTrue(found, "List should contain country code " + expectedCountryCode);
    }


    @Then("the summary should contain total capital saved {string}")
    @SuppressWarnings("unchecked")
    public void theSummaryShouldContainTotalCapitalSaved(String expectedValue) {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = (Map<String, Object>) testContext.getLastResponse().getBody();
        assertNotNull(body, "Response body should not be null");
        
        Object totalCapitalSaved = body.get("totalCapitalSaved");
        assertNotNull(totalCapitalSaved, "totalCapitalSaved should not be null");
        
        BigDecimal expected = new BigDecimal(expectedValue);
        BigDecimal actual = totalCapitalSaved instanceof BigDecimal 
            ? (BigDecimal) totalCapitalSaved 
            : new BigDecimal(totalCapitalSaved.toString());
        
        assertEquals(0, expected.compareTo(actual), 
            "Expected total capital saved to be " + expectedValue + " but was " + actual);
    }

    @Then("the summary should contain total capital loaned {string}")
    @SuppressWarnings("unchecked")
    public void theSummaryShouldContainTotalCapitalLoaned(String expectedValue) {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = (Map<String, Object>) testContext.getLastResponse().getBody();
        assertNotNull(body, "Response body should not be null");
        
        Object totalCapitalLoaned = body.get("totalCapitalLoaned");
        assertNotNull(totalCapitalLoaned, "totalCapitalLoaned should not be null");
        
        BigDecimal expected = new BigDecimal(expectedValue);
        BigDecimal actual = totalCapitalLoaned instanceof BigDecimal 
            ? (BigDecimal) totalCapitalLoaned 
            : new BigDecimal(totalCapitalLoaned.toString());
        
        assertEquals(0, expected.compareTo(actual), 
            "Expected total capital loaned to be " + expectedValue + " but was " + actual);
    }

    @Then("the summary should contain total profits generated {string}")
    @SuppressWarnings("unchecked")
    public void theSummaryShouldContainTotalProfitsGenerated(String expectedValue) {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = (Map<String, Object>) testContext.getLastResponse().getBody();
        assertNotNull(body, "Response body should not be null");
        
        Object totalProfitsGenerated = body.get("totalProfitsGenerated");
        assertNotNull(totalProfitsGenerated, "totalProfitsGenerated should not be null");
        
        BigDecimal expected = new BigDecimal(expectedValue);
        BigDecimal actual = totalProfitsGenerated instanceof BigDecimal 
            ? (BigDecimal) totalProfitsGenerated 
            : new BigDecimal(totalProfitsGenerated.toString());
        
        assertEquals(0, expected.compareTo(actual), 
            "Expected total profits generated to be " + expectedValue + " but was " + actual);
    }

    @Then("the summary should contain grand total {string}")
    @SuppressWarnings("unchecked")
    public void theSummaryShouldContainGrandTotal(String expectedValue) {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = (Map<String, Object>) testContext.getLastResponse().getBody();
        assertNotNull(body, "Response body should not be null");
        
        Object grandTotal = body.get("grandTotal");
        assertNotNull(grandTotal, "grandTotal should not be null");
        
        BigDecimal expected = new BigDecimal(expectedValue);
        BigDecimal actual = grandTotal instanceof BigDecimal 
            ? (BigDecimal) grandTotal 
            : new BigDecimal(grandTotal.toString());
        
        assertEquals(0, expected.compareTo(actual), 
            "Expected grand total to be " + expectedValue + " but was " + actual);
    }

    @Then("the summary should contain {int} countries")
    @SuppressWarnings("unchecked")
    public void theSummaryShouldContainCountries(int expectedCount) {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = (Map<String, Object>) testContext.getLastResponse().getBody();
        assertNotNull(body, "Response body should not be null");
        
        Object byCountry = body.get("byCountry");
        assertNotNull(byCountry, "byCountry should not be null");
        List<?> countryList = (List<?>) byCountry;
        
        assertEquals(expectedCount, countryList.size(), 
            "Expected summary to contain " + expectedCount + " countries");
    }

    @Then("the summary should contain country {string} with capital saved {string}")
    @SuppressWarnings("unchecked")
    public void theSummaryShouldContainCountryWithCapitalSaved(String countryCode, String expectedCapitalSaved) {
        assertNotNull(testContext.getLastResponse(), "Response should not be null");
        Map<String, Object> body = (Map<String, Object>) testContext.getLastResponse().getBody();
        assertNotNull(body, "Response body should not be null");
        
        List<Map<String, Object>> byCountry = (List<Map<String, Object>>) body.get("byCountry");
        assertNotNull(byCountry, "byCountry should not be null");
        
        Map<String, Object> countryData = byCountry.stream()
            .filter(item -> countryCode.equals(item.get("countryCode")))
            .findFirst()
            .orElse(null);
        
        assertNotNull(countryData, "Country " + countryCode + " should be in summary");
        
        Object capitalSaved = countryData.get("capitalSaved");
        assertNotNull(capitalSaved, "capitalSaved should not be null");
        
        BigDecimal expected = new BigDecimal(expectedCapitalSaved);
        BigDecimal actual = capitalSaved instanceof BigDecimal 
            ? (BigDecimal) capitalSaved 
            : new BigDecimal(capitalSaved.toString());
        
        assertEquals(0, expected.compareTo(actual), 
            "Expected capital saved for " + countryCode + " to be " + expectedCapitalSaved + " but was " + actual);
    }
}