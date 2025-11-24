Feature: Update transaction

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

  Scenario: Update an existing transaction
    Given the user has a valid authorization token
    When a transaction with id '1de572a9-8deb-49c7-88b7-4f353722b224' is updated for budget item 'ef91a488-e596-44cc-ac02-5fd2b166f8c6' and budget '8fca0def-5086-4cae-af5e-11a217288806':
      | date                     | amount | transactionType | merchant  | notes  |
      | 2025-09-28T05:47:26.853Z | 25.75  | EXPENSE         | Rock Auto | Filter |
    Then the request response status is 'OK'
    And the following transactions are returned:
      | date                     | amount | transactionType | merchant  | notes  | budgetId                             | budgetItemId                         |
      | 2025-09-28T05:47:26.853Z | 25.75  | EXPENSE         | Rock Auto | Filter | 8fca0def-5086-4cae-af5e-11a217288806 | ef91a488-e596-44cc-ac02-5fd2b166f8c6 |
    And the following transactions will exist:
      | date                     | amount  | transactionType | merchant  | notes  | budgetId                             | budgetItemId                         |
      | 2025-09-28T05:47:26.853Z | 25.7500 | EXPENSE         | Rock Auto | Filter | 8fca0def-5086-4cae-af5e-11a217288806 | ef91a488-e596-44cc-ac02-5fd2b166f8c6 |
      | 2025-09-28T05:45:26.853Z | 16.0000 | EXPENSE         | Autozone  |        | 8fca0def-5086-4cae-af5e-11a217288806 | ef91a488-e596-44cc-ac02-5fd2b166f8c6 |
    And the application will log the following messages:
      | level | message                                                           |
      | INFO  | Updating transaction with id 1de572a9-8deb-49c7-88b7-4f353722b224 |

  Scenario: Do not update a transaction that does not exist
    Given the user has a valid authorization token
    When a transaction with id '2b50aac9-f8b8-4363-a307-e44c7d6453f4' is updated for budget item 'ef91a488-e596-44cc-ac02-5fd2b166f8c6' and budget '8fca0def-5086-4cae-af5e-11a217288806':
      | date                     | amount | transactionType | merchant  | notes  |
      | 2025-09-28T05:47:26.853Z | 25.75  | EXPENSE         | Rock Auto | Filter |
    Then the request response status is 'NOT_FOUND'
    And the following transactions are returned:
      | date | amount | transactionType | merchant | notes | budgetId | budgetItemId |
    And the following transactions will exist:
      | date                     | amount  | transactionType | merchant  | notes       | budgetId                             | budgetItemId                         |
      | 2025-09-28T05:47:26.853Z | 50.2500 | EXPENSE         | Rock Auto | Fuel Filter | 8fca0def-5086-4cae-af5e-11a217288806 | ef91a488-e596-44cc-ac02-5fd2b166f8c6 |
      | 2025-09-28T05:45:26.853Z | 16.0000 | EXPENSE         | Autozone  |             | 8fca0def-5086-4cae-af5e-11a217288806 | ef91a488-e596-44cc-ac02-5fd2b166f8c6 |
    And the application will log the following messages:
      | level | message                                                                                                                               |
      | ERROR | Transaction does not exist for budget item id ef91a488-e596-44cc-ac02-5fd2b166f8c6 and budget id 8fca0def-5086-4cae-af5e-11a217288806 |

  Scenario: Do not update a transaction with a zero amount
    Given the user has a valid authorization token
    When a transaction with id '1de572a9-8deb-49c7-88b7-4f353722b224' is updated for budget item 'ef91a488-e596-44cc-ac02-5fd2b166f8c6' and budget '8fca0def-5086-4cae-af5e-11a217288806':
      | date                     | amount | transactionType | merchant  | notes  |
      | 2025-09-28T05:47:26.853Z | 0.00   | EXPENSE         | Rock Auto | Filter |
    Then the request response status is 'BAD_REQUEST'
    And the following transactions are returned:
      | date | amount | transactionType | merchant | notes | budgetId | budgetItemId |
    And the following transactions will exist:
      | date                     | amount  | transactionType | merchant  | notes       | budgetId                             | budgetItemId                         |
      | 2025-09-28T05:47:26.853Z | 50.2500 | EXPENSE         | Rock Auto | Fuel Filter | 8fca0def-5086-4cae-af5e-11a217288806 | ef91a488-e596-44cc-ac02-5fd2b166f8c6 |
      | 2025-09-28T05:45:26.853Z | 16.0000 | EXPENSE         | Autozone  |             | 8fca0def-5086-4cae-af5e-11a217288806 | ef91a488-e596-44cc-ac02-5fd2b166f8c6 |
    And the application will log the following messages:
      | level | message                                                                                                                               |
      | ERROR | IllegalArgumentException: Transaction amount must be positive                                                                       |

  Scenario: Transactions cannot be updated with an invalid authorization token
    Given the user has an invalid authorization token
    When a transaction with id 'ef91a488-e596-44cc-ac02-5fd2b166f8c6' is updated for budget item 'ef91a488-e596-44cc-ac02-5fd2b166f8c6' and budget '8fca0def-5086-4cae-af5e-11a217288806':
      | date                     | amount | transactionType | merchant  | notes  |
      | 2025-09-28T05:47:26.853Z | 25.75  | EXPENSE         | Rock Auto | Filter |
    Then the request response status is 'UNAUTHORIZED'
    And the following transactions are returned:
      | date | amount | transactionType | merchant | notes | budgetId | budgetItemId |
    And the following transactions will exist:
      | date                     | amount  | transactionType | merchant  | notes       | budgetId                             | budgetItemId                         |
      | 2025-09-28T05:47:26.853Z | 50.2500 | EXPENSE         | Rock Auto | Fuel Filter | 8fca0def-5086-4cae-af5e-11a217288806 | ef91a488-e596-44cc-ac02-5fd2b166f8c6 |
      | 2025-09-28T05:45:26.853Z | 16.0000 | EXPENSE         | Autozone  |             | 8fca0def-5086-4cae-af5e-11a217288806 | ef91a488-e596-44cc-ac02-5fd2b166f8c6 |
    And the application will log the following messages:
      | level | message |
