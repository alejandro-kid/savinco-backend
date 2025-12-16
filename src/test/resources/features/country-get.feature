Feature: Get Country
  As a financial administrator
  I want to retrieve country information
  So that I can view country details and their currencies

  # SYNC: Happy path - Get country by code
  Scenario: Successfully get country by code
    Given the API is running
    And currency exists with code "USD"
    And country exists with code "ECU" and name "Ecuador" and currencyCode "USD"
    When I get country by code "ECU"
    Then I should receive status code 200 immediately
    And the response should contain country code "ECU"
    And the response should contain country name "Ecuador"
    And the response should contain currency code "USD"

  # SYNC: Happy path - List all countries
  Scenario: Successfully list all countries
    Given the API is running
    And currency exists with code "USD"
    And currency exists with code "EUR"
    And country exists with code "ECU" and currencyCode "USD"
    And country exists with code "ESP" and currencyCode "EUR"
    When I list all countries
    Then I should receive status code 200 immediately
    And the response should contain 2 countries
    And the response should contain country with code "ECU"
    And the response should contain country with code "ESP"

  # SYNC: Error - Country not found
  Scenario: Fail to get country when code does not exist
    Given the API is running
    And no country exists with code "INVALID"
    When I get country by code "INVALID"
    Then I should receive status code 404 immediately
    And the response should contain error message about country not found

