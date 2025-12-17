Feature: Update Financial Data Record
  As a financial administrator
  I want to update financial data records for countries
  So that I can modify capital saved, capital loaned, and profits generated

  # SYNC: Happy path - Update existing country data
  Scenario: Successfully update financial data for existing country
    Given the API is running
    And financial data exists for country "ECU" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | USD          | 1000000.00   | 5000000.00    | 500000.00        |
    When I update financial data for country "ECU" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | USD          | 2000000.00   | 6000000.00    | 600000.00        |
    Then I should receive status code 200 immediately
    And the response should contain country code "ECU"
    And the response should contain country name "Ecuador"
    And the response should contain original currency "USD"
    And the response should contain capital saved in USD "2000000.00"
    And the response should contain capital loaned in USD "6000000.00"
    And the response should contain profits generated in USD "600000.00"
    And the response should contain total in USD "8600000.00"

  # SYNC: Happy path - Update with EUR (values in original currency)
  Scenario: Successfully update financial data with EUR to USD conversion
    Given the API is running
    And financial data exists for country "ESP" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | EUR          | 1000000.00   | 5000000.00    | 500000.00        |
    When I update financial data for country "ESP" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | EUR          | 2000000.00   | 10000000.00   | 1000000.00       |
    Then I should receive status code 200 immediately
    And the response should contain country code "ESP"
    And the response should contain country name "España"
    And the response should contain original currency "EUR"
    And the response should contain capital saved in USD "2000000.00"
    And the response should contain capital loaned in USD "10000000.00"
    And the response should contain profits generated in USD "1000000.00"
    And the response should contain total in USD "13000000.00"

  # SYNC: Error - Update country that does not exist
  Scenario: Fail to update financial data when country does not exist
    Given the API is running
    And no financial data exists for country "ECU"
    When I update financial data for country "ECU" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | USD          | 2000000.00   | 6000000.00    | 600000.00        |
    Then I should receive status code 404 immediately
    And the response should contain error message about country not found

  # SYNC: Error - Country code mismatch (path vs body)
  Scenario: Fail to update when country code in path does not match body
    Given the API is running
    And financial data exists for country "ECU" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | USD          | 1000000.00   | 5000000.00    | 500000.00        |
    When I update financial data for country "ECU" with body containing country code "ESP":
      | countryCode | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | ESP         | USD          | 2000000.00   | 6000000.00    | 600000.00        |
    Then I should receive status code 400 immediately
    And the response should contain error message about country code mismatch

  # SYNC: Error - Invalid currency code
  Scenario: Fail to update financial data with invalid currency code
    Given the API is running
    And financial data exists for country "ECU" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | USD          | 1000000.00   | 5000000.00    | 500000.00        |
    When I update financial data for country "ECU" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | INVALID      | 2000000.00   | 6000000.00    | 600000.00        |
    Then I should receive status code 400 immediately
    And the response should contain error message about invalid currency code

  # SYNC: Error - Currency does not match country
  Scenario: Fail to update financial data when currency does not match country
    Given the API is running
    And financial data exists for country "ESP" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | EUR          | 1000000.00   | 5000000.00    | 500000.00        |
    When I update financial data for country "ESP" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | USD          | 2000000.00   | 6000000.00    | 600000.00        |
    Then I should receive status code 400 immediately
    And the response should contain error message about currency mismatch

  # SYNC: Error - Negative values
  Scenario: Fail to update financial data with negative capital saved
    Given the API is running
    And financial data exists for country "PER" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | PEN          | 1000000.00   | 5000000.00    | 500000.00        |
    When I update financial data for country "PER" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | PEN          | -1000000.00  | 5000000.00    | 500000.00        |
    Then I should receive status code 400 immediately
    And the response should contain error message about negative values

  # SYNC: Edge case - Update with zero values (should be allowed)
  Scenario: Successfully update financial data with zero values
    Given the API is running
    And financial data exists for country "NPL" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | NPR          | 1000000.00   | 5000000.00    | 500000.00        |
    When I update financial data for country "NPL" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | NPR          | 0.00         | 0.00          | 0.00             |
    Then I should receive status code 200 immediately
    And the response should contain country code "NPL"
    And the response should contain capital saved in USD "0.00"
    And the response should contain capital loaned in USD "0.00"
    And the response should contain profits generated in USD "0.00"
    And the response should contain total in USD "0.00"

  # SYNC: Error - Invalid country code in path
  Scenario: Fail to update financial data with invalid country code in path
    Given the API is running
    When I update financial data for country "INVALID" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | USD          | 2000000.00   | 6000000.00    | 600000.00        |
    Then I should receive status code 400 immediately
    And the response should contain error message about invalid country code