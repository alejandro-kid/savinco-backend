Feature: Get Financial Data Records
  As a financial administrator
  I want to retrieve financial data records for countries
  So that I can view capital saved, capital loaned, and profits generated

  # SYNC: Happy path - GET all countries (empty list)
  Scenario: Successfully get all financial data when no records exist
    Given the API is running
    When I request all financial data
    Then I should receive status code 200 immediately
    And the response should be an empty list

  # SYNC: Happy path - GET all countries (with data)
  Scenario: Successfully get all financial data with existing records
    Given the API is running
    And financial data exists for country "ECU" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | USD          | 1000000.00   | 5000000.00    | 500000.00        |
    And financial data exists for country "ESP" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | EUR          | 1000000.00   | 5000000.00    | 500000.00        |
    When I request all financial data
    Then I should receive status code 200 immediately
    And the response should be a list with 2 items
    And the list should contain country code "ECU"
    And the list should contain country code "ESP"

  # SYNC: Happy path - GET by country code (exists)
  Scenario: Successfully get financial data by country code
    Given the API is running
    And financial data exists for country "ECU" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | USD          | 1000000.00   | 5000000.00    | 500000.00        |
    When I request financial data for country "ECU"
    Then I should receive status code 200 immediately
    And the response should contain country code "ECU"
    And the response should contain country name "Ecuador"
    And the response should contain original currency "USD"
    And the response should contain capital saved in USD "1000000.00"
    And the response should contain capital loaned in USD "5000000.00"
    And the response should contain profits generated in USD "500000.00"
    And the response should contain total in USD "6500000.00"

  # SYNC: Happy path - GET by country code with currency conversion
  Scenario: Successfully get financial data by country code with EUR conversion
    Given the API is running
    And financial data exists for country "ESP" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | EUR          | 1000000.00   | 5000000.00    | 500000.00        |
    When I request financial data for country "ESP"
    Then I should receive status code 200 immediately
    And the response should contain country code "ESP"
    And the response should contain country name "España"
    And the response should contain original currency "EUR"
    And the response should contain capital saved in USD "1111111.11"
    And the response should contain capital loaned in USD "5555555.56"
    And the response should contain profits generated in USD "555555.56"
    And the response should contain total in USD "7222222.23"

  # SYNC: Error - GET by country code (not found)
  Scenario: Fail to get financial data when country does not exist
    Given the API is running
    And no financial data exists for country "ECU"
    When I request financial data for country "ECU"
    Then I should receive status code 404 immediately
    And the response should contain error message about country not found

  # SYNC: Error - GET by country code (invalid code)
  Scenario: Fail to get financial data with invalid country code
    Given the API is running
    When I request financial data for country "INVALID"
    Then I should receive status code 400 immediately
    And the response should contain error message about invalid country code

  # SYNC: Happy path - GET summary (empty)
  Scenario: Successfully get summary when no financial data exists
    Given the API is running
    When I request financial data summary
    Then I should receive status code 200 immediately
    And the summary should contain total capital saved "0.00"
    And the summary should contain total capital loaned "0.00"
    And the summary should contain total profits generated "0.00"
    And the summary should contain grand total "0.00"
    And the summary should contain 0 countries

  # SYNC: Happy path - GET summary with consolidated data
  Scenario: Successfully get summary with consolidated financial data
    Given the API is running
    And financial data exists for country "ECU" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | USD          | 1000000.00   | 5000000.00    | 500000.00        |
    And financial data exists for country "ESP" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | EUR          | 1000000.00   | 5000000.00    | 500000.00        |
    When I request financial data summary
    Then I should receive status code 200 immediately
    And the summary should contain total capital saved "2111111.11"
    And the summary should contain total capital loaned "10555555.56"
    And the summary should contain total profits generated "1055555.56"
    And the summary should contain grand total "13722222.23"
    And the summary should contain 2 countries
    And the summary should contain country "ECU" with capital saved "1000000.00"
    And the summary should contain country "ESP" with capital saved "1111111.11"

  # SYNC: Edge case - GET summary with all four countries
  Scenario: Successfully get summary with all four countries
    Given the API is running
    And financial data exists for country "ECU" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | USD          | 1000000.00   | 5000000.00    | 500000.00        |
    And financial data exists for country "ESP" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | EUR          | 1000000.00   | 5000000.00    | 500000.00        |
    And financial data exists for country "PER" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | PEN          | 3300000.00   | 16500000.00   | 1650000.00       |
    And financial data exists for country "NPL" with:
      | currencyCode | capitalSaved | capitalLoaned | profitsGenerated |
      | NPR          | 133000000.00 | 665000000.00  | 66500000.00      |
    When I request financial data summary
    Then I should receive status code 200 immediately
    And the summary should contain 4 countries