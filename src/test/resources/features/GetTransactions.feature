Feature: Get transactions for a budget item and budget

  Background:
    Given the following budgets exist:
      | id                                   | budgetMonth | budgetYear |
      | 8fca0def-5086-4cae-af5e-11a217288806 | 9           | 2025       |
    And the following budget items exist:
      | id                                   | name            | plannedAmount | budgetId                             | categoryName   |
      | ef91a488-e596-44cc-ac02-5fd2b166f8c6 | Car Maintenance | 100.75        | 8fca0def-5086-4cae-af5e-11a217288806 | Transportation |
      | 8a23ba21-eb0d-4751-b531-9e46a979009f | Groceries       | 200.00        | 8fca0def-5086-4cae-af5e-11a217288806 | Food           |
    And the following transactions exist:
      | date                     | amount | transactionType | merchant  | notes       | budgetItemId                         | budgetId                             |
      | 2025-09-28T05:47:26.853Z | 50.25  | EXPENSE         | Rock Auto | Fuel Filter | ef91a488-e596-44cc-ac02-5fd2b166f8c6 | 8fca0def-5086-4cae-af5e-11a217288806 |
      | 2025-09-28T05:45:26.853Z | 16.00  | EXPENSE         | Autozone  |             | ef91a488-e596-44cc-ac02-5fd2b166f8c6 | 8fca0def-5086-4cae-af5e-11a217288806 |
      | 2025-09-28T05:47:26.853Z | 45.50  | EXPENSE         | Martins   |             | 8a23ba21-eb0d-4751-b531-9e46a979009f | 8fca0def-5086-4cae-af5e-11a217288806 |


  Scenario: Get all transactions for a budget item and budget
    Given the user has a valid authorization token
    When all transactions are retrieved for budget item 'ef91a488-e596-44cc-ac02-5fd2b166f8c6' and  budget '8fca0def-5086-4cae-af5e-11a217288806'
    Then the request response status is 'OK'
    And the following transactions are returned:
      | date                     | amount | transactionType | merchant  | notes       | budgetId                             | budgetItemId                         |
      | 2025-09-28T05:47:26.853Z | 50.25  | EXPENSE         | Rock Auto | Fuel Filter | 8fca0def-5086-4cae-af5e-11a217288806 | ef91a488-e596-44cc-ac02-5fd2b166f8c6 |
    And the application will log the following messages:
      | level | message                                                                                                                                |
      | INFO  | Retrieving all transactions for budget item id ef91a488-e596-44cc-ac02-5fd2b166f8c6 and budget id 8fca0def-5086-4cae-af5e-11a217288806 |

  Scenario: Transactions cannot be retrieved with an invalid authorization token
    Given the user has an invalid authorization token
    When all transactions are retrieved for budget item 'ef91a488-e596-44cc-ac02-5fd2b166f8c6' and  budget '8fca0def-5086-4cae-af5e-11a217288806'
    Then the request response status is 'UNAUTHORIZED'
    And the following transactions are returned:
      | date | amount | transactionType | merchant | notes | budgetId | budgetItemId |
    And the application will log the following messages:
      | level | message |