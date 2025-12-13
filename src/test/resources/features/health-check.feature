Feature: Health Check Endpoint
  As a system administrator
  I want to check the health status of the API
  So that I can verify the service is running and operational

  # SYNC: Happy path with immediate response
  Scenario: Successfully check API health status
    Given the API is running
    When I request the health check endpoint
    Then I should receive status code 200 immediately
    And the response should contain status "UP"
    And the response should contain timestamp
