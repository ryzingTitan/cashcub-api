Feature: Get budget items for a budget

  Background:
    Given the following budgets exist:
      | id                                   | budgetMonth | budgetYear |
      | 8fca0def-5086-4cae-af5e-11a217288806 | 9           | 2025       |
      | 8da4019e-4f65-42f8-8215-4ca8a935ada4 | 10          | 2025       |
    And the following budget items exist:
      | name            | plannedAmount | budgetId                             | categoryName   |
      | Car Maintenance | 100.75        | 8fca0def-5086-4cae-af5e-11a217288806 | Transportation |
      | Groceries       | 200.00        | 8fca0def-5086-4cae-af5e-11a217288806 | Food           |
      | Groceries       | 250.00        | 8da4019e-4f65-42f8-8215-4ca8a935ada4 | Food           |

  Scenario: Get all budget items for a budget
    Given the user has a valid authorization token
    When all budget items are retrieved for budget '8fca0def-5086-4cae-af5e-11a217288806'
    Then the request response status is 'OK'
    And the following budget items are returned:
      | name            | plannedAmount | budgetId                             | categoryName   |
      | Car Maintenance | 100.75        | 8fca0def-5086-4cae-af5e-11a217288806 | Transportation |
      | Groceries       | 200.00        | 8fca0def-5086-4cae-af5e-11a217288806 | Food           |
    And the application will log the following messages:
      | level | message                                                                        |
      | INFO  | Retrieving all budget items for budget id 8fca0def-5086-4cae-af5e-11a217288806 |

  Scenario: Budget items cannot be retrieved with an invalid authorization token
    Given the user has an invalid authorization token
    When all budget items are retrieved for budget '8fca0def-5086-4cae-af5e-11a217288806'
    Then the request response status is 'UNAUTHORIZED'
    And the following budgets are returned:
      | month | year |
    And the application will log the following messages:
      | level | message |