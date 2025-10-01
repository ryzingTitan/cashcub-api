Feature: Get a budget summary

  Background:
    Given the following budgets exist:
      | id                                   | budgetMonth | budgetYear |
      | 8fca0def-5086-4cae-af5e-11a217288806 | 9           | 2025       |
      | d6c4b213-c67f-4fac-8965-a696a6308fc1 | 10          | 2025       |
    And the following budget items exist:
      | id                                   | name            | plannedAmount | budgetId                             | categoryName   |
      | ef91a488-e596-44cc-ac02-5fd2b166f8c6 | Car Maintenance | 100.75        | 8fca0def-5086-4cae-af5e-11a217288806 | Transportation |
      | 8a23ba21-eb0d-4751-b531-9e46a979009f | Groceries       | 200.00        | 8fca0def-5086-4cae-af5e-11a217288806 | Food           |
      | b7617450-7ce6-4810-97ea-6caea9f9e8e5 | First Paycheck  | 1200.00       | 8fca0def-5086-4cae-af5e-11a217288806 | Income         |
      | 8f80e5b7-3838-4204-bc98-8d301c46f210 | Second Paycheck | 1200.00       | 8fca0def-5086-4cae-af5e-11a217288806 | Income         |
      | a2042d8b-3f4c-4cd8-a4c5-a601e7cd2196 | Second Paycheck | 1200.00       | d6c4b213-c67f-4fac-8965-a696a6308fc1 | Income         |
    And the following transactions exist:
      | date                     | amount  | transactionType | merchant  | notes       | budgetItemId                         | budgetId                             |
      | 2025-09-28T05:47:26.853Z | 50.25   | EXPENSE         | Rock Auto | Fuel Filter | ef91a488-e596-44cc-ac02-5fd2b166f8c6 | 8fca0def-5086-4cae-af5e-11a217288806 |
      | 2025-09-28T05:45:26.853Z | 16.00   | EXPENSE         | Autozone  |             | ef91a488-e596-44cc-ac02-5fd2b166f8c6 | 8fca0def-5086-4cae-af5e-11a217288806 |
      | 2025-09-28T05:47:26.853Z | 45.50   | EXPENSE         | Martins   |             | 8a23ba21-eb0d-4751-b531-9e46a979009f | 8fca0def-5086-4cae-af5e-11a217288806 |
      | 2025-09-30T05:47:26.853Z | 1200.00 | INCOME          |           |             | b7617450-7ce6-4810-97ea-6caea9f9e8e5 | 8fca0def-5086-4cae-af5e-11a217288806 |

  Scenario: Get budget summary
    Given the user has a valid authorization token
    When a budget summary is retrieved for budget id '8fca0def-5086-4cae-af5e-11a217288806'
    Then the request response status is 'OK'
    And the following budget summaries are returned:
      | month | year | expectedIncome | actualIncome | expectedExpenses | actualExpenses |
      | 9     | 2025 | 2400.00        | 1200.00      | 300.75           | 111.75         |
    And the following budget items are returned in the summary:
      | name            | plannedAmount | actualAmount | budgetId                             | categoryName   |
      | Car Maintenance | 100.75        | 66.25        | 8fca0def-5086-4cae-af5e-11a217288806 | Transportation |
      | Groceries       | 200.00        | 45.50        | 8fca0def-5086-4cae-af5e-11a217288806 | Food           |
      | First Paycheck  | 1200.00       | 1200.00      | 8fca0def-5086-4cae-af5e-11a217288806 | Income         |
      | Second Paycheck | 1200.00       | 0.00         | 8fca0def-5086-4cae-af5e-11a217288806 | Income         |
    And the application will log the following messages:
      | level | message                                                                      |
      | INFO  | Retrieving budget summary for budget id 8fca0def-5086-4cae-af5e-11a217288806 |

  Scenario: Do not return budget data when budget does not exist
    Given the user has a valid authorization token
    When a budget summary is retrieved for budget id 'f88fa462-1ab0-444b-96b9-c19fdf0e13a2'
    Then the request response status is 'NOT_FOUND'
    And the following budget summaries are returned:
      | month | year | expectedIncome | actualIncome | expectedExpenses | actualExpenses |
    And the application will log the following messages:
      | level | message                                                                      |
      | INFO  | Retrieving budget summary for budget id f88fa462-1ab0-444b-96b9-c19fdf0e13a2 |
      | ERROR | Budget with id f88fa462-1ab0-444b-96b9-c19fdf0e13a2 does not exist           |

  Scenario: Budget data cannot be retrieved with an invalid authorization token
    Given the user has an invalid authorization token
    When a budget summary is retrieved for budget id '8fca0def-5086-4cae-af5e-11a217288806'
    Then the request response status is 'UNAUTHORIZED'
    And the following budget summaries are returned:
      | month | year | expectedIncome | actualIncome | expectedExpenses | actualExpenses |
    And the application will log the following messages:
      | level | message |