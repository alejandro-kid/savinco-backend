Feature: Delete Financial Data Record
  As a financial administrator
  I want to delete financial data records for countries
  So that I can remove data that is no longer needed

  # SYNC: Happy path - Delete existing country data
  Scenario: Successfully delete financial data for existing country
    Given the API is running
    And financial data exists for country "ECU" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | USD          | 1000000.00   | 5000000.00    | 500000.00        |
    When I delete financial data for country "ECU"
    Then I should receive status code 204 immediately
    And the response body should be empty

  # SYNC: Error - Delete country that does not exist
  Scenario: Fail to delete financial data when country does not exist
    Given the API is running
    And no financial data exists for country "ECU"
    When I delete financial data for country "ECU"
    Then I should receive status code 404 immediately
    And the response should contain error message about country not found

  # SYNC: Error - Invalid country code
  Scenario: Fail to delete financial data with invalid country code
    Given the API is running
    When I delete financial data for country "INVALID"
    Then I should receive status code 400 immediately
    And the response should contain error message about invalid country code

  # SYNC: Edge case - Verify deletion by trying to get deleted country
  Scenario: Verify deletion by attempting to get deleted country data
    Given the API is running
    And financial data exists for country "ESP" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | EUR          | 1000000.00   | 5000000.00    | 500000.00        |
    When I delete financial data for country "ESP"
    Then I should receive status code 204 immediately
    When I request financial data for country "ESP"
    Then I should receive status code 404 immediately
    And the response should contain error message about country not found