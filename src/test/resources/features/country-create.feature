Feature: Create Country
  As a financial administrator
  I want to create country records
  So that I can manage countries and their associated currencies

  # SYNC: Happy path - Create country with valid currency
  Scenario: Successfully create a country
    Given the API is running
    And currency exists with code "USD" and name "US Dollar"
    And no country exists with code "ECU"
    When I create country with:
      | code | name    | currencyCode |
      | ECU  | Ecuador | USD          |
    Then I should receive status code 201 immediately
    And the response should contain country code "ECU"
    And the response should contain country name "Ecuador"
    And the response should contain currency code "USD"

  # SYNC: Error - Duplicate country code
  Scenario: Fail to create country when code already exists
    Given the API is running
    And currency exists with code "USD"
    And country exists with code "ECU" and name "Ecuador"
    When I create country with:
      | code | name    | currencyCode |
      | ECU  | Ecuador | USD          |
    Then I should receive status code 409 immediately
    And the response should contain error message about duplicate country code

  # SYNC: Error - Invalid country code format
  Scenario: Fail to create country with invalid code format
    Given the API is running
    And currency exists with code "USD"
    When I create country with:
      | code    | name    | currencyCode |
      | INVALID | Ecuador | USD          |
    Then I should receive status code 400 immediately
    And the response should contain error message about invalid country code format

  # SYNC: Error - Currency does not exist
  Scenario: Fail to create country with non-existent currency
    Given the API is running
    And no currency exists with code "ZZZ"
    And no country exists with code "ECU"
    When I create country with:
      | code | name    | currencyCode |
      | ECU  | Ecuador | ZZZ          |
    Then I should receive status code 404 immediately
    And the response should contain error message about currency not found

  # SYNC: Error - Missing required fields
  Scenario: Fail to create country with missing required fields
    Given the API is running
    And currency exists with code "USD"
    When I create country with:
      | code | name | currencyCode |
      |      | Ecuador | USD       |
    Then I should receive status code 400 immediately
    And the response should contain error message about missing required fields

