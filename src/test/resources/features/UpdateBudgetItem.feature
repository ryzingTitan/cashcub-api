Feature: Update budget item

  Background:
    Given the following budgets exist:
      | id                                   | budgetMonth | budgetYear |
      | 8fca0def-5086-4cae-af5e-11a217288806 | 9           | 2025       |
    And the following budget items exist:
      | id                                   | name            | plannedAmount | budgetId                             | categoryName   |
      | ef91a488-e596-44cc-ac02-5fd2b166f8c6 | Car Maintenance | 100.75        | 8fca0def-5086-4cae-af5e-11a217288806 | Transportation |
      | 8a23ba21-eb0d-4751-b531-9e46a979009f | Groceries       | 200.00        | 8fca0def-5086-4cae-af5e-11a217288806 | Food           |

  Scenario: Update an existing budget item
    Given the user has a valid authorization token
    When a budget item with id 'ef91a488-e596-44cc-ac02-5fd2b166f8c6' is updated with the following data for budget '8fca0def-5086-4cae-af5e-11a217288806':
      | name            | plannedAmount | categoryName   |
      | Car Maintenance | 250           | Transportation |
    Then the request response status is 'OK'
    And the following budget items are returned:
      | name            | plannedAmount | budgetId                             | categoryName   |
      | Car Maintenance | 250           | 8fca0def-5086-4cae-af5e-11a217288806 | Transportation |
    And the following budget items will exist:
      | name            | plannedAmount | budgetId                             | categoryName   |
      | Car Maintenance | 250.0000      | 8fca0def-5086-4cae-af5e-11a217288806 | Transportation |
      | Groceries       | 200.0000      | 8fca0def-5086-4cae-af5e-11a217288806 | Food           |
    And the application will log the following messages:
      | level | message                                                           |
      | INFO  | Updating budget item with id ef91a488-e596-44cc-ac02-5fd2b166f8c6 |

  Scenario: Do not update a budget item that does not exist
    Given the user has a valid authorization token
    When a budget item with id '388f4192-6e21-4ab0-80f0-fbf99a50d755' is updated with the following data for budget '8fca0def-5086-4cae-af5e-11a217288806':
      | name            | plannedAmount | categoryName   |
      | Car Maintenance | 250           | Transportation |
    Then the request response status is 'NOT_FOUND'
    And the following budget items are returned:
      | name | plannedAmount | budgetId | categoryName |
    And the following budget items will exist:
      | name            | plannedAmount | budgetId                             | categoryName   |
      | Car Maintenance | 100.7500      | 8fca0def-5086-4cae-af5e-11a217288806 | Transportation |
      | Groceries       | 200.0000      | 8fca0def-5086-4cae-af5e-11a217288806 | Food           |
    And the application will log the following messages:
      | level | message                                                                                                 |
      | ERROR | Budget item with name Car Maintenance does not exist for budget id 8fca0def-5086-4cae-af5e-11a217288806 |

  Scenario: Budget items cannot be updated with an invalid authorization token
    Given the user has an invalid authorization token
    When a budget item with id 'ef91a488-e596-44cc-ac02-5fd2b166f8c6' is updated with the following data for budget '8fca0def-5086-4cae-af5e-11a217288806':
      | name            | plannedAmount | categoryName   |
      | Car Maintenance | 250           | Transportation |
    Then the request response status is 'UNAUTHORIZED'
    And the following budget items are returned:
      | name | plannedAmount | budgetId | categoryName |
    And the application will log the following messages:
      | level | message |