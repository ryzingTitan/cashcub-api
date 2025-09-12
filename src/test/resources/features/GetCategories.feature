Feature: Get categories

  Scenario: Get all categories
    Given the user has a valid authorization token
    When all categories are retrieved
    Then the request response status is 'OK'
    And the following categories are returned:
      | name           |
      | Income         |
      | Giving         |
      | Savings        |
      | Housing        |
      | Transportation |
      | Food           |
      | Personal       |
      | Lifestyle      |
      | Health         |
      | Insurance      |
    And the application will log the following messages:
      | level | message                   |
      | INFO  | Retrieving all categories |

  Scenario: Categories cannot be retrieved with an invalid authorization token
    Given the user has an invalid authorization token
    When all categories are retrieved
    Then the request response status is 'UNAUTHORIZED'
    And the following categories are returned:
      | name |
    And the application will log the following messages:
      | level | message |