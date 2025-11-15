Feature: Get budget summaries for analytics

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

  Scenario: Get budget summaries for a single month
    Given the user has a valid authorization token
    When analytics data is retrieved for date range from '9-2025' to '9-2025'
    Then the request response status is 'OK'
    And the following budget summaries are returned:
      | month | year | expectedIncome | actualIncome | expectedExpenses | actualExpenses |
      | 9     | 2025 | 2400.00        | 1200.00      | 300.75           | 111.75         |
    And the following budget items are returned in the summary for budget '8fca0def-5086-4cae-af5e-11a217288806':
      | name            | plannedAmount | actualAmount | budgetId                             | categoryName   |
      | Car Maintenance | 100.75        | 66.25        | 8fca0def-5086-4cae-af5e-11a217288806 | Transportation |
      | Groceries       | 200.00        | 45.50        | 8fca0def-5086-4cae-af5e-11a217288806 | Food           |
      | First Paycheck  | 1200.00       | 1200.00      | 8fca0def-5086-4cae-af5e-11a217288806 | Income         |
      | Second Paycheck | 1200.00       | 0.00         | 8fca0def-5086-4cae-af5e-11a217288806 | Income         |
    And the application will log the following messages:
      | level | message                                                                      |
      | INFO  | Retrieving all budgets from 9-2025 to 9-2025                                 |
      | INFO  | Retrieving budget summary for budget id 8fca0def-5086-4cae-af5e-11a217288806 |

  Scenario: Get budget summaries for a multiple months
    Given the user has a valid authorization token
    When analytics data is retrieved for date range from '9-2025' to '10-2025'
    Then the request response status is 'OK'
    And the following budget summaries are returned:
      | month | year | expectedIncome | actualIncome | expectedExpenses | actualExpenses |
      | 9     | 2025 | 2400.00        | 1200.00      | 300.75           | 111.75         |
      | 10    | 2025 | 1200.00        | 0.00         | 0.00             | 0.00           |
    And the following budget items are returned in the summary for budget '8fca0def-5086-4cae-af5e-11a217288806':
      | name            | plannedAmount | actualAmount | budgetId                             | categoryName   |
      | Car Maintenance | 100.75        | 66.25        | 8fca0def-5086-4cae-af5e-11a217288806 | Transportation |
      | Groceries       | 200.00        | 45.50        | 8fca0def-5086-4cae-af5e-11a217288806 | Food           |
      | First Paycheck  | 1200.00       | 1200.00      | 8fca0def-5086-4cae-af5e-11a217288806 | Income         |
      | Second Paycheck | 1200.00       | 0.00         | 8fca0def-5086-4cae-af5e-11a217288806 | Income         |
    And the following budget items are returned in the summary for budget 'd6c4b213-c67f-4fac-8965-a696a6308fc1':
      | name            | plannedAmount | actualAmount | budgetId                             | categoryName |
      | Second Paycheck | 1200.00       | 0.00         | d6c4b213-c67f-4fac-8965-a696a6308fc1 | Income       |
    And the application will log the following messages:
      | level | message                                                                      |
      | INFO  | Retrieving all budgets from 9-2025 to 10-2025                                |
      | INFO  | Retrieving budget summary for budget id 8fca0def-5086-4cae-af5e-11a217288806 |
      | INFO  | Retrieving budget summary for budget id d6c4b213-c67f-4fac-8965-a696a6308fc1 |

  Scenario: Analytics data cannot be retrieved with an invalid authorization token
    Given the user has an invalid authorization token
    When analytics data is retrieved for date range from '9-2025' to '10-2025'
    Then the request response status is 'UNAUTHORIZED'
    And the following budget summaries are returned:
      | month | year | expectedIncome | actualIncome | expectedExpenses | actualExpenses |
    And the application will log the following messages:
      | level | message |