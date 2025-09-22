Feature: Get budgets

  Background:
    Given the following budgets exist:
      | month | year |
      | 9     | 2025 |
      | 10    | 2025 |

  Scenario: Get all budgets
    Given the user has a valid authorization token
    When all budgets are retrieved
    Then the request response status is 'OK'
    And the following budgets are returned:
      | month | year |
      | 9     | 2025 |
      | 10    | 2025 |
    And the application will log the following messages:
      | level | message                |
      | INFO  | Retrieving all budgets |

  Scenario: Budgets cannot be retrieved with an invalid authorization token
    Given the user has an invalid authorization token
    When all budgets are retrieved
    Then the request response status is 'UNAUTHORIZED'
    And the following budgets are returned:
      | month | year |
    And the application will log the following messages:
      | level | message |