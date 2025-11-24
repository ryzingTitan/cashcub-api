Feature: Delete budget item

  Background:
    Given the following budgets exist:
      | id                                   | budgetMonth | budgetYear |
      | 8fca0def-5086-4cae-af5e-11a217288806 | 9           | 2025       |
    And the following budget items exist:
      | id                                   | name            | plannedAmount | budgetId                             | categoryName   |
      | ef91a488-e596-44cc-ac02-5fd2b166f8c6 | Car Maintenance | 100.75        | 8fca0def-5086-4cae-af5e-11a217288806 | Transportation |
      | 8a23ba21-eb0d-4751-b531-9e46a979009f | Groceries       | 200.00        | 8fca0def-5086-4cae-af5e-11a217288806 | Food           |
    And the following transactions exist:
      | id                                   | date                     | amount | transactionType | merchant  | notes       | budgetItemId                         | budgetId                             |
      | 1de572a9-8deb-49c7-88b7-4f353722b224 | 2025-09-28T05:47:26.853Z | 50.25  | EXPENSE         | Rock Auto | Fuel Filter | ef91a488-e596-44cc-ac02-5fd2b166f8c6 | 8fca0def-5086-4cae-af5e-11a217288806 |
      | 39c08e9c-1514-47d8-b79a-8075414b3646 | 2025-09-28T05:45:26.853Z | 16.00  | EXPENSE         | Autozone  |             | ef91a488-e596-44cc-ac02-5fd2b166f8c6 | 8fca0def-5086-4cae-af5e-11a217288806 |

  Scenario: Delete an existing budget item
    Given the user has a valid authorization token
    When a budget item with id 'ef91a488-e596-44cc-ac02-5fd2b166f8c6' is deleted for budget '8fca0def-5086-4cae-af5e-11a217288806'
    Then the request response status is 'NO_CONTENT'
    And the following budget items will exist:
      | name      | plannedAmount | budgetId                             | categoryName |
      | Groceries | 200.0000      | 8fca0def-5086-4cae-af5e-11a217288806 | Food         |
    And the following transactions will exist:
      | date | amount | transactionType | merchant | notes | budgetId | budgetItemId |
    And the application will log the following messages:
      | level | message                                                                                                               |
      | INFO  | Deleting budget item with id ef91a488-e596-44cc-ac02-5fd2b166f8c6 from budget id 8fca0def-5086-4cae-af5e-11a217288806 |

  Scenario: Do not delete a budget item that does not exist
    Given the user has a valid authorization token
    When a budget item with id '388f4192-6e21-4ab0-80f0-fbf99a50d755' is deleted for budget '8fca0def-5086-4cae-af5e-11a217288806'
    Then the request response status is 'NO_CONTENT'
    And the following budget items will exist:
      | name            | plannedAmount | budgetId                             | categoryName   |
      | Car Maintenance | 100.7500      | 8fca0def-5086-4cae-af5e-11a217288806 | Transportation |
      | Groceries       | 200.0000      | 8fca0def-5086-4cae-af5e-11a217288806 | Food           |
    And the following transactions will exist:
      | id                                   | date                     | amount  | transactionType | merchant  | notes       | budgetItemId                         | budgetId                             |
      | 1de572a9-8deb-49c7-88b7-4f353722b224 | 2025-09-28T05:47:26.853Z | 50.2500 | EXPENSE         | Rock Auto | Fuel Filter | ef91a488-e596-44cc-ac02-5fd2b166f8c6 | 8fca0def-5086-4cae-af5e-11a217288806 |
      | 39c08e9c-1514-47d8-b79a-8075414b3646 | 2025-09-28T05:45:26.853Z | 16.0000 | EXPENSE         | Autozone  |             | ef91a488-e596-44cc-ac02-5fd2b166f8c6 | 8fca0def-5086-4cae-af5e-11a217288806 |
    And the application will log the following messages:
      | level | message                                                                                                               |
      | INFO  | Deleting budget item with id 388f4192-6e21-4ab0-80f0-fbf99a50d755 from budget id 8fca0def-5086-4cae-af5e-11a217288806 |

  Scenario: Budget items cannot be deleted with an invalid authorization token
    Given the user has an invalid authorization token
    When a budget item with id 'ef91a488-e596-44cc-ac02-5fd2b166f8c6' is deleted for budget '8fca0def-5086-4cae-af5e-11a217288806'
    Then the request response status is 'UNAUTHORIZED'
    And the following budget items will exist:
      | name            | plannedAmount | budgetId                             | categoryName   |
      | Car Maintenance | 100.7500      | 8fca0def-5086-4cae-af5e-11a217288806 | Transportation |
      | Groceries       | 200.0000      | 8fca0def-5086-4cae-af5e-11a217288806 | Food           |
    And the following transactions will exist:
      | id                                   | date                     | amount  | transactionType | merchant  | notes       | budgetItemId                         | budgetId                             |
      | 1de572a9-8deb-49c7-88b7-4f353722b224 | 2025-09-28T05:47:26.853Z | 50.2500 | EXPENSE         | Rock Auto | Fuel Filter | ef91a488-e596-44cc-ac02-5fd2b166f8c6 | 8fca0def-5086-4cae-af5e-11a217288806 |
      | 39c08e9c-1514-47d8-b79a-8075414b3646 | 2025-09-28T05:45:26.853Z | 16.0000 | EXPENSE         | Autozone  |             | ef91a488-e596-44cc-ac02-5fd2b166f8c6 | 8fca0def-5086-4cae-af5e-11a217288806 |
    And the application will log the following messages:
      | level | message |