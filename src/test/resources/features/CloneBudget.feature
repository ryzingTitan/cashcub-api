Feature: Clone an existing budget

  Background:
    Given the following budgets exist:
      | id                                   | budgetMonth | budgetYear |
      | 8fca0def-5086-4cae-af5e-11a217288806 | 9           | 2025       |
    And the following budget items exist:
      | id                                   | name            | plannedAmount | budgetId                             | categoryName   |
      | ef91a488-e596-44cc-ac02-5fd2b166f8c6 | Car Maintenance | 100.75        | 8fca0def-5086-4cae-af5e-11a217288806 | Transportation |
      | 8a23ba21-eb0d-4751-b531-9e46a979009f | Groceries       | 200.00        | 8fca0def-5086-4cae-af5e-11a217288806 | Food           |

  Scenario: Clone a budget from an existing budget
    Given the user has a valid authorization token
    When a budget with id '8fca0def-5086-4cae-af5e-11a217288806' is cloned for month 10 and year 2025
    Then the request response status is 'CREATED'
    And the following budget summaries are returned:
      | month | year | expectedIncome | actualIncome | expectedExpenses | actualExpenses |
      | 10    | 2025 | 0.00           | 0.00         | 300.75           | 0.00           |
    And the following budget items are returned in the summary:
      | name            | plannedAmount | actualAmount | budgetId                             | categoryName   |
      | Car Maintenance | 100.75        | 0.00         | 00000000-0000-0000-0000-000000000000 | Transportation |
      | Groceries       | 200.00        | 0.00         | 00000000-0000-0000-0000-000000000000 | Food           |
    And the following budgets will exist:
      | budgetMonth | budgetYear |
      | 9           | 2025       |
      | 10          | 2025       |
    And the following budget items will exist:
      | name            | plannedAmount | budgetId                             | categoryName   |
      | Car Maintenance | 100.7500      | 8fca0def-5086-4cae-af5e-11a217288806 | Transportation |
      | Groceries       | 200.0000      | 8fca0def-5086-4cae-af5e-11a217288806 | Food           |
      | Car Maintenance | 100.7500      | 00000000-0000-0000-0000-000000000000 | Transportation |
      | Groceries       | 200.0000      | 00000000-0000-0000-0000-000000000000 | Food           |

  Scenario: Do not create a duplicate a budget
    Given the user has a valid authorization token
    And the following budgets exist:
      | budgetMonth | budgetYear |
      | 10          | 2025       |
    When a budget with id '8fca0def-5086-4cae-af5e-11a217288806' is cloned for month 10 and year 2025
    Then the request response status is 'CONFLICT'
    And the following budget summaries are returned:
      | month | year | expectedIncome | actualIncome | expectedExpenses | actualExpenses |
    And the following budgets will exist:
      | budgetMonth | budgetYear |
      | 9           | 2025       |
      | 10          | 2025       |
    And the following budget items will exist:
      | name            | plannedAmount | budgetId                             | categoryName   |
      | Car Maintenance | 100.7500      | 8fca0def-5086-4cae-af5e-11a217288806 | Transportation |
      | Groceries       | 200.0000      | 8fca0def-5086-4cae-af5e-11a217288806 | Food           |
    And the application will log the following messages:
      | level | message                                          |
      | ERROR | Budget already exists for month 10 and year 2025 |

  Scenario: Budgets cannot be cloned with an invalid authorization token
    Given the user has an invalid authorization token
    When a budget with id '8fca0def-5086-4cae-af5e-11a217288806' is cloned for month 10 and year 2025
    Then the request response status is 'UNAUTHORIZED'
    And the following budget summaries are returned:
      | month | year | expectedIncome | actualIncome | expectedExpenses | actualExpenses |
    And the following budgets will exist:
      | budgetMonth | budgetYear |
      | 9           | 2025       |
    And the following budget items will exist:
      | name            | plannedAmount | budgetId                             | categoryName   |
      | Car Maintenance | 100.7500      | 8fca0def-5086-4cae-af5e-11a217288806 | Transportation |
      | Groceries       | 200.0000      | 8fca0def-5086-4cae-af5e-11a217288806 | Food           |
    And the application will log the following messages:
      | level | message |