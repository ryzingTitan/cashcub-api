Feature: Create transaction

  Background:
    Given the following budgets exist:
      | id                                   | budgetMonth | budgetYear |
      | 8fca0def-5086-4cae-af5e-11a217288806 | 9           | 2025       |
    And the following budget items exist:
      | id                                   | name            | plannedAmount | budgetId                             | categoryName   |
      | ef91a488-e596-44cc-ac02-5fd2b166f8c6 | Car Maintenance | 100.75        | 8fca0def-5086-4cae-af5e-11a217288806 | Transportation |
      | 8a23ba21-eb0d-4751-b531-9e46a979009f | Groceries       | 200.00        | 8fca0def-5086-4cae-af5e-11a217288806 | Food           |

  Scenario: Create a new transaction
    Given the user has a valid authorization token
    When a transaction is created with the following data for budget '8fca0def-5086-4cae-af5e-11a217288806' and budget item 'ef91a488-e596-44cc-ac02-5fd2b166f8c6':
      | date                     | amount | transactionType | merchant  | notes       |
      | 2025-09-28T05:47:26.853Z | 50.25  | EXPENSE         | Rock Auto | Fuel Filter |
    Then the request response status is 'CREATED'
    And the following transactions are returned:
      | date                     | amount | transactionType | merchant  | notes       | budgetId                             | budgetItemId                         |
      | 2025-09-28T05:47:26.853Z | 50.25  | EXPENSE         | Rock Auto | Fuel Filter | 8fca0def-5086-4cae-af5e-11a217288806 | ef91a488-e596-44cc-ac02-5fd2b166f8c6 |
    And the following transactions will exist:
      | date                     | amount  | transactionType | merchant  | notes       | budgetId                             | budgetItemId                         |
      | 2025-09-28T05:47:26.853Z | 50.2500 | EXPENSE         | Rock Auto | Fuel Filter | 8fca0def-5086-4cae-af5e-11a217288806 | ef91a488-e596-44cc-ac02-5fd2b166f8c6 |
    And the application will log the following messages:
      | level | message                                                                                                                           |
      | INFO  | Creating a transaction for budget item id ef91a488-e596-44cc-ac02-5fd2b166f8c6 and budget id 8fca0def-5086-4cae-af5e-11a217288806 |

  Scenario: Transactions cannot be created with an invalid authorization token
    Given the user has an invalid authorization token
    When a transaction is created with the following data for budget '8fca0def-5086-4cae-af5e-11a217288806' and budget item 'ef91a488-e596-44cc-ac02-5fd2b166f8c6':
      | date                     | amount | transactionType | merchant  | notes       |
      | 2025-09-28T05:47:26.853Z | 50.25  | EXPENSE         | Rock Auto | Fuel Filter |
    Then the request response status is 'UNAUTHORIZED'
    And the following transactions are returned:
      | date | amount | transactionType | merchant | notes | budgetId | budgetItemId |
    And the application will log the following messages:
      | level | message |