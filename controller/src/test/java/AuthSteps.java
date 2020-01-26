import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class AuthSteps {

    @Given("^Username \"([^\"]*)\" and password \"([^\"]*)\"$")
    public void usernameAndPassword(String username, String password) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        System.out.println(String.format("Username: %s, Password: %s",username, password));
        throw new PendingException();
    }

    @When("^Access Login$")
    public void accessLogin() {
    }

    @Then("^User details returned$")
    public void userDetailsReturned() {
    }
}
