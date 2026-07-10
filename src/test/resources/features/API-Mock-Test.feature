@api @mock
Feature: API test with WireMock - Mock API Testing

  Scenario: Should see LIST USERS from mock endpoint
    Given I open the API endpoint
    Given I get the default list of users for page 1
    When I get the list of all users within every page
    Then I should see total users count equals the number of user ids
    And the response header "Content-Type" should contain "application/json"

  Scenario: Should see SINGLE USER NOT FOUND from mock endpoint
    Given I open the API endpoint
    Given I make a search for user 55
    Then I receive error code 404 in response

  Scenario: LOGIN - SUCCESSFUL from mock endpoint
    Given I open the API endpoint
    Given I login successfully with the following data
      | Email              | Password   |
      | eve.holt@reqres.in | cityslicka |
    Then I should get a response code of 200
    And the response header "Content-Type" should contain "application/json"

