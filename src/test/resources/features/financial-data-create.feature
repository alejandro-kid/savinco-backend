Feature: Create Financial Data Record
  As a financial administrator
  I want to create financial data records for countries
  So that I can track capital saved, capital loaned, and profits generated

  # SYNC: Happy path with immediate response
  Scenario: Successfully create financial data for Ecuador
    Given the API is running
    And no financial data exists for country "ECU"
    When I create financial data with:
      | countryCode | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | ECU         | USD          | 1000000.00   | 5000000.00    | 500000.00        |
    Then I should receive status code 201 immediately
    And the response should contain country code "ECU"
    And the response should contain country name "Ecuador"
    And the response should contain original currency "USD"
    And the response should contain capital saved in USD "1000000.00"
    And the response should contain capital loaned in USD "5000000.00"
    And the response should contain profits generated in USD "500000.00"
    And the response should contain total in USD "6500000.00"

  # SYNC: Happy path - Create for Spain with EUR (values in original currency)
  Scenario: Successfully create financial data for Spain with EUR to USD conversion
    Given the API is running
    And no financial data exists for country "ESP"
    When I create financial data with:
      | countryCode | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | ESP         | EUR          | 1000000.00   | 5000000.00    | 500000.00        |
    Then I should receive status code 201 immediately
    And the response should contain country code "ESP"
    And the response should contain country name "España"
    And the response should contain original currency "EUR"
    And the response should contain capital saved in USD "1000000.00"
    And the response should contain capital loaned in USD "5000000.00"
    And the response should contain profits generated in USD "500000.00"
    And the response should contain total in USD "6500000.00"

  # SYNC: Error - Duplicate country (most common error)
  Scenario: Fail to create financial data when country already exists
    Given the API is running
    And financial data exists for country "ECU" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | USD          | 1000000.00   | 5000000.00    | 500000.00        |
    When I create financial data with:
      | countryCode | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | ECU         | USD          | 2000000.00   | 6000000.00    | 600000.00        |
    Then I should receive status code 409 immediately
    And the response should contain error message about duplicate country

  # SYNC: Error - Invalid country code
  Scenario: Fail to create financial data with invalid country code
    Given the API is running
    When I create financial data with:
      | countryCode | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | INVALID     | USD          | 1000000.00   | 5000000.00    | 500000.00        |
    Then I should receive status code 400 immediately
    And the response should contain error message about invalid country code

  # SYNC: Error - Currency code does not match country
  Scenario: Fail to create financial data when currency does not match country
    Given the API is running
    And no financial data exists for country "ESP"
    When I create financial data with:
      | countryCode | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | ESP         | USD          | 1000000.00   | 5000000.00    | 500000.00        |
    Then I should receive status code 400 immediately
    And the response should contain error message about currency mismatch

  # SYNC: Error - Negative values
  Scenario: Fail to create financial data with negative capital saved
    Given the API is running
    And no financial data exists for country "PER"
    When I create financial data with:
      | countryCode | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | PER         | PEN          | -1000000.00  | 5000000.00    | 500000.00        |
    Then I should receive status code 400 immediately
    And the response should contain error message about negative values

  # SYNC: Edge case - Zero values (should be allowed)
  Scenario: Successfully create financial data with zero values
    Given the API is running
    And no financial data exists for country "NPL"
    When I create financial data with:
      | countryCode | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | NPL         | NPR          | 0.00         | 0.00          | 0.00             |
    Then I should receive status code 201 immediately
    And the response should contain country code "NPL"
    And the response should contain capital saved in USD "0.00"
    And the response should contain capital loaned in USD "0.00"
    And the response should contain profits generated in USD "0.00"
    And the response should contain total in USD "0.00"

  # SYNC: Edge case - Large decimal precision
  Scenario: Successfully create financial data with decimal precision
    Given the API is running
    And no financial data exists for country "PER"
    When I create financial data with:
      | countryCode | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | PER         | PEN          | 1234567.89   | 9876543.21    | 555555.55        |
    Then I should receive status code 201 immediately
    And the response should contain country code "PER"
    And the response should contain original currency "PEN"
    And the response should contain capital saved in USD "1234567.89"
    And the response should contain capital loaned in USD "9876543.21"
    And the response should contain profits generated in USD "555555.55"

  # SYNC: Bug fix validation - Create after delete (validates country_id and currency_id are properly set)
  Scenario: Successfully create financial data after deleting previous record
    Given the API is running
    And financial data exists for country "ESP" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | EUR          | 1000000.00   | 5000000.00    | 500000.00        |
    When I delete financial data for country "ESP"
    Then I should receive status code 204 immediately
    When I create financial data with:
      | countryCode | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | ESP         | EUR          | 2000000.00   | 6000000.00    | 600000.00        |
    Then I should receive status code 201 immediately
    And the response should contain country code "ESP"
    And the response should contain country name "España"
    And the response should contain original currency "EUR"
    And the response should contain capital saved in USD "2000000.00"
    And the response should contain capital loaned in USD "6000000.00"
    And the response should contain profits generated in USD "600000.00"
    And the response should contain total in USD "8600000.00"
