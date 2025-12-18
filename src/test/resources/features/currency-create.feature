Feature: Create Currency
  As a financial administrator
  I want to create currency records
  So that I can manage currencies and their exchange rates

  # SYNC: Happy path - Create first currency (automatically becomes base)
  Scenario: Successfully create first currency (automatically becomes base)
    Given the API is running
    And no currency exists with code "USD"
    When I create currency with:
      | code | name      | isBase | exchangeRateToBase |
      | USD  | US Dollar | false  | 2.00              |
    Then I should receive status code 201 immediately
    And the response should contain currency code "USD"
    And the response should contain currency name "US Dollar"
    And the response should contain isBase "true"
    And the response should contain exchangeRateToBase "1.00"

  # SYNC: Happy path - Create non-base currency (after base exists)
  Scenario: Successfully create a non-base currency
    Given the API is running
    And currency exists with code "USD" and name "US Dollar"
    And no currency exists with code "EUR"
    When I create currency with:
      | code | name      | isBase | exchangeRateToBase |
      | EUR  | Euro      | false  | 0.90              |
    Then I should receive status code 201 immediately
    And the response should contain currency code "EUR"
    And the response should contain currency name "Euro"
    And the response should contain isBase "false"
    And the response should contain exchangeRateToBase "0.90"

  # SYNC: Happy path - Create base currency (USD) - first currency
  Scenario: Successfully create base currency USD (first currency)
    Given the API is running
    And no currency exists with code "USD"
    When I create currency with:
      | code | name      | isBase | exchangeRateToBase |
      | USD  | US Dollar | true   | 1.00              |
    Then I should receive status code 201 immediately
    And the response should contain currency code "USD"
    And the response should contain currency name "US Dollar"
    And the response should contain isBase "true"
    And the response should contain exchangeRateToBase "1.00"

  # SYNC: Error - Duplicate currency code
  Scenario: Fail to create currency when code already exists
    Given the API is running
    And currency exists with code "USD" and name "US Dollar"
    When I create currency with:
      | code | name      | isBase | exchangeRateToBase |
      | USD  | US Dollar | true   | 1.00              |
    Then I should receive status code 409 immediately
    And the response should contain error message about duplicate currency code

  # SYNC: Error - Invalid currency code format
  Scenario: Fail to create currency with invalid code format
    Given the API is running
    When I create currency with:
      | code | name      | isBase | exchangeRateToBase |
      | INVALID | Invalid Currency | false | 0.50 |
    Then I should receive status code 400 immediately
    And the response should contain error message about invalid currency code format

  # SYNC: Error - Try to create second base currency
  Scenario: Fail to create currency when another base currency exists
    Given the API is running
    And currency exists with code "USD" and name "US Dollar"
    When I create currency with:
      | code | name      | isBase | exchangeRateToBase |
      | EUR  | Euro      | true   | 0.90              |
    Then I should receive status code 400 immediately
    And the response should contain error message about base currency already exists

  # SYNC: Error - Negative exchange rate
  Scenario: Fail to create currency with negative exchange rate
    Given the API is running
    And no currency exists with code "EUR"
    When I create currency with:
      | code | name | isBase | exchangeRateToBase |
      | EUR  | Euro | false  | -0.90             |
    Then I should receive status code 400 immediately
    And the response should contain error message about negative exchange rate

  # SYNC: Error - Zero exchange rate for non-base currency
  Scenario: Fail to create non-base currency with zero exchange rate
    Given the API is running
    And currency exists with code "USD" and name "US Dollar"
    And no currency exists with code "EUR"
    When I create currency with:
      | code | name | isBase | exchangeRateToBase |
      | EUR  | Euro | false  | 0.00              |
    Then I should receive status code 400 immediately
    And the response should contain error message about invalid exchange rate

  # SYNC: Edge case - Missing required fields
  Scenario: Fail to create currency with missing required fields
    Given the API is running
    When I create currency with:
      | code | name | isBase | exchangeRateToBase |
      |      | Euro | false  | 0.90              |
    Then I should receive status code 400 immediately
    And the response should contain error message about missing required fields

