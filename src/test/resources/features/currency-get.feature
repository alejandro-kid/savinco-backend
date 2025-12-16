Feature: Get Currency
  As a financial administrator
  I want to retrieve currency information
  So that I can view currency details and exchange rates

  # SYNC: Happy path - Get currency by code
  Scenario: Successfully get currency by code
    Given the API is running
    And currency exists with code "USD" and name "US Dollar"
    When I get currency by code "USD"
    Then I should receive status code 200 immediately
    And the response should contain currency code "USD"
    And the response should contain currency name "US Dollar"
    And the response should contain isBase "true"
    And the response should contain exchangeRateToBase "1.00"

  # SYNC: Happy path - List all currencies
  Scenario: Successfully list all currencies
    Given the API is running
    And currency exists with code "USD" and name "US Dollar"
    And currency exists with code "EUR" and name "Euro"
    And currency exists with code "PEN" and name "Peruvian Sol"
    When I list all currencies
    Then I should receive status code 200 immediately
    And the response should contain 3 currencies
    And the response should contain currency with code "USD"
    And the response should contain currency with code "EUR"
    And the response should contain currency with code "PEN"

  # SYNC: Error - Currency not found
  Scenario: Fail to get currency when code does not exist
    Given the API is running
    And no currency exists with code "ZZZ"
    When I get currency by code "ZZZ"
    Then I should receive status code 404 immediately
    And the response should contain error message about currency not found

  # SYNC: Happy path - Get base currency
  Scenario: Successfully get base currency
    Given the API is running
    And currency exists with code "USD" and isBase "true"
    When I get base currency
    Then I should receive status code 200 immediately
    And the response should contain currency code "USD"
    And the response should contain isBase "true"
    And the response should contain exchangeRateToBase "1.00"

