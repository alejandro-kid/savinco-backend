Feature: Update Currency Exchange Rate
  As a financial administrator
  I want to update currency exchange rates
  So that I can reflect current market rates

  # SYNC: Happy path - Update exchange rate for non-base currency
  Scenario: Successfully update exchange rate for non-base currency
    Given the API is running
    And currency exists with code "USD" and name "US Dollar"
    And currency exists with code "EUR" and exchangeRateToBase "0.90"
    When I update exchange rate for currency "EUR" to "0.95"
    Then I should receive status code 200 immediately
    And the response should contain currency code "EUR"
    And the response should contain exchangeRateToBase "0.95"

  # SYNC: Error - Try to update base currency exchange rate
  Scenario: Fail to update exchange rate for base currency
    Given the API is running
    And currency exists with code "USD" and name "US Dollar"
    When I update exchange rate for currency "USD" to "1.10"
    Then I should receive status code 400 immediately
    And the response should contain error message about cannot update base currency rate

  # SYNC: Error - Currency not found
  Scenario: Fail to update exchange rate when currency does not exist
    Given the API is running
    And no currency exists with code "ZZZ"
    When I update exchange rate for currency "ZZZ" to "0.90"
    Then I should receive status code 404 immediately
    And the response should contain error message about currency not found

  # SYNC: Error - Negative exchange rate
  Scenario: Fail to update exchange rate with negative value
    Given the API is running
    And currency exists with code "USD" and name "US Dollar"
    And currency exists with code "EUR" and exchangeRateToBase "0.90"
    When I update exchange rate for currency "EUR" to "-0.90"
    Then I should receive status code 400 immediately
    And the response should contain error message about negative exchange rate

  # SYNC: Error - Zero exchange rate for non-base currency
  Scenario: Fail to update exchange rate to zero for non-base currency
    Given the API is running
    And currency exists with code "USD" and name "US Dollar"
    And currency exists with code "EUR" and exchangeRateToBase "0.90"
    When I update exchange rate for currency "EUR" to "0.00"
    Then I should receive status code 400 immediately
    And the response should contain error message about invalid exchange rate

  # SYNC: Edge case - Update with same value (should succeed)
  Scenario: Successfully update exchange rate with same value
    Given the API is running
    And currency exists with code "USD" and name "US Dollar"
    And currency exists with code "EUR" and exchangeRateToBase "0.90"
    When I update exchange rate for currency "EUR" to "0.90"
    Then I should receive status code 200 immediately
    And the response should contain exchangeRateToBase "0.90"

