Feature: Delete Country
  As a financial administrator
  I want to delete country records
  So that I can remove countries that are no longer needed

  # SYNC: Happy path - Delete country without associated financial data
  Scenario: Successfully delete country without associated financial data
    Given the API is running
    And currency exists with code "USD" and name "US Dollar"
    And country exists with code "ECU" and currencyCode "USD"
    And no financial data exists for country "ECU"
    When I delete country with code "ECU"
    Then I should receive status code 204 immediately
    And the response body should be empty

  # SYNC: Error - Cannot delete country with associated financial data
  Scenario: Fail to delete country when financial data is associated
    Given the API is running
    And currency exists with code "USD" and name "US Dollar"
    And country exists with code "ECU" and currencyCode "USD"
    And financial data exists for country "ECU" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | USD          | 1000000.00   | 5000000.00    | 500000.00        |
    When I delete country with code "ECU"
    Then I should receive status code 409 immediately
    And the response should contain error message about country having associated financial data

  # SYNC: Error - Delete country that does not exist
  Scenario: Fail to delete country when country does not exist
    Given the API is running
    And no country exists with code "ZZZ"
    When I delete country with code "ZZZ"
    Then I should receive status code 404 immediately
    And the response should contain error message about country not found

  # SYNC: Error - Invalid country code
  Scenario: Fail to delete country with invalid code
    Given the API is running
    When I delete country with code "INVALID"
    Then I should receive status code 400 immediately
    And the response should contain error message about invalid country code format
