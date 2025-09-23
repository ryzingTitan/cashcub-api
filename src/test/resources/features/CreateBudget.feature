Feature: Create budget

  Scenario: Create a new budget
    Given the user has a valid authorization token
    When a budget is created for month 10 and year 2025
    Then the request response status is 'CREATED'
    And the following budgets are returned:
      | month | year |
      | 10    | 2025 |
    And the following budgets will exist:
      | budgetMonth | budgetYear |
      | 10          | 2025       |
    And the application will log the following messages:
      | level | message                                      |
      | INFO  | Creating a budget for month 10 and year 2025 |

  Scenario: Do not create a duplicate a budget
    Given the user has a valid authorization token
    And the following budgets exist:
      | budgetMonth | budgetYear |
      | 10          | 2025       |
    When a budget is created for month 10 and year 2025
    Then the request response status is 'CONFLICT'
    And the following budgets are returned:
      | month | year |
    And the following budgets will exist:
      | budgetMonth | budgetYear |
      | 10          | 2025       |
    And the application will log the following messages:
      | level | message                                          |
      | ERROR | Budget already exists for month 10 and year 2025 |

  Scenario: Budgets cannot be created with an invalid authorization token
    Given the user has an invalid authorization token
    When a budget is created for month 10 and year 2025
    Then the request response status is 'UNAUTHORIZED'
    And the following budgets are returned:
      | month | year |
    And the application will log the following messages:
      | level | message |