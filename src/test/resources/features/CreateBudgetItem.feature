Feature: Create budget item

  Background:
    Given the following budgets exist:
      | id                                   | budgetMonth | budgetYear |
      | 8fca0def-5086-4cae-af5e-11a217288806 | 9           | 2025       |

  Scenario: Create a new budget item
    Given the user has a valid authorization token
    When a budget item is created with the following data for budget '8fca0def-5086-4cae-af5e-11a217288806':
      | name            | plannedAmount | categoryName   |
      | Car Maintenance | 100.75        | Transportation |
    Then the request response status is 'CREATED'
    And the following budget items are returned:
      | name            | plannedAmount | budgetId                             | categoryName   |
      | Car Maintenance | 100.75        | 8fca0def-5086-4cae-af5e-11a217288806 | Transportation |
    And the following budget items will exist:
      | name            | plannedAmount | budgetId                             | categoryName   |
      | Car Maintenance | 100.7500      | 8fca0def-5086-4cae-af5e-11a217288806 | Transportation |
    And the application will log the following messages:
      | level | message                                                                                             |
      | INFO  | Creating a budget item with name Car Maintenance for budget id 8fca0def-5086-4cae-af5e-11a217288806 |

  Scenario: Do not create a duplicate a budget item
    Given the user has a valid authorization token
    And the following budget items exist:
      | name            | plannedAmount | budgetId                             | categoryName   |
      | Car Maintenance | 100.75        | 8fca0def-5086-4cae-af5e-11a217288806 | Transportation |
    When a budget item is created with the following data for budget '8fca0def-5086-4cae-af5e-11a217288806':
      | name            | plannedAmount | categoryName   |
      | Car Maintenance | 100.75        | Transportation |
    Then the request response status is 'CONFLICT'
    And the following budget items are returned:
      | name | plannedAmount | budgetId | categoryName |
    And the following budget items will exist:
      | name            | plannedAmount | budgetId                             | categoryName   |
      | Car Maintenance | 100.7500      | 8fca0def-5086-4cae-af5e-11a217288806 | Transportation |
    And the application will log the following messages:
      | level | message                                                                                                |
      | ERROR | Budget item already exists for name Car Maintenance and budget id 8fca0def-5086-4cae-af5e-11a217288806 |

  Scenario: Budget items cannot be created with an invalid authorization token
    Given the user has an invalid authorization token
    When a budget item is created with the following data for budget '8fca0def-5086-4cae-af5e-11a217288806':
      | name            | plannedAmount | categoryName   |
      | Car Maintenance | 100.75        | Transportation |
    Then the request response status is 'UNAUTHORIZED'
    And the following budget items are returned:
      | name | plannedAmount | budgetId | categoryName |
    And the following budget items will exist:
      | name | plannedAmount | budgetId | categoryName |
    And the application will log the following messages:
      | level | message |