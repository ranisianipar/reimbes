Feature: Authentication
  This feature is used to handle user authentication

  Scenario: Login
    Given Username "chrisevan" and password "chrisevan123"
    When Access Login
    Then User details returned