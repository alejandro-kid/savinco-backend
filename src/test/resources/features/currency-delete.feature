Feature: Delete Currency
  As a financial administrator
  I want to delete currency records
  So that I can remove currencies that are no longer needed

  # SYNC: Happy path - Delete currency without associated countries
  Scenario: Successfully delete currency without associated countries
    Given the API is running
    And currency exists with code "EUR" and name "Euro"
    And no country exists with currency code "EUR"
    When I delete currency with code "EUR"
    Then I should receive status code 204 immediately
    And the response body should be empty

  # SYNC: Error - Cannot delete currency with associated countries
  Scenario: Fail to delete currency when countries are associated
    Given the API is running
    And currency exists with code "EUR" and name "Euro"
    And country exists with code "ESP" and currencyCode "EUR"
    When I delete currency with code "EUR"
    Then I should receive status code 409 immediately
    And the response should contain error message about currency having associated countries

  # SYNC: Error - Delete currency that does not exist
  Scenario: Fail to delete currency when currency does not exist
    Given the API is running
    And no currency exists with code "ZZZ"
    When I delete currency with code "ZZZ"
    Then I should receive status code 404 immediately
    And the response should contain error message about currency not found

  # SYNC: Error - Invalid currency code
  Scenario: Fail to delete currency with invalid code
    Given the API is running
    When I delete currency with code "INVALID"
    Then I should receive status code 400 immediately
    And the response should contain error message about invalid currency code format
