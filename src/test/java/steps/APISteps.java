package steps;
import api.LoginClient;
import api.UserClient;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class APISteps {
    private final UserClient userClient = new UserClient();
    private final LoginClient loginClient = new LoginClient();
    private Response response;
    private final List<Integer> allUserIds = new ArrayList<>();
    private int expectedTotalUsers;

    @Given("I open the API endpoint")
    public void iOpenTheAPIEndpoint() {
        response = userClient.getUsers(1);
    }

    @Given("I get the default list of users for page {int}")
    public void iGetTheDefaultListofusers(Integer page) {
        response = userClient.getUsers(page);

    }

    @When("I get the list of all users within every page")
    public void iGetTheListOfAllUsers() {
        if (response == null) {
            response = userClient.getUsers(1);
        }

        allUserIds.clear();
        expectedTotalUsers = response.jsonPath().getInt("total");
        int totalPages = response.jsonPath().getInt("total_pages");

        collectUserIds(response);

        for (int page = 2; page <= totalPages; page++) {
            response = userClient.getUsers(page);
            collectUserIds(response);
        }
    }

    @Then("I should see total users count equals the number of user ids")
    public void iShouldMatchTotalCount() {
        Assert.assertEquals(
                allUserIds.size(),
                expectedTotalUsers,
                "Total users count does not match number of IDs returned across all pages.");
    }

    private void collectUserIds(Response pageResponse) {
        List<Integer> pageIds = pageResponse.jsonPath().getList("data.id", Integer.class);
        if (pageIds != null) {
            allUserIds.addAll(pageIds);
        }
    }

    @Given("I make a search for user {int}")
    public void iMakeASearchForUser(int userId) {
        response = userClient.getUser(userId);

    }

    @Then("I should see the following user data")
    public void IShouldSeeFollowingUserData(DataTable table) {
        Map<String, String> expected =
                table.asMaps(String.class, String.class).get(0);

        Assert.assertEquals(
                response.jsonPath().getString("data.first_name"),
                expected.get("first_name"));

        Assert.assertEquals(
                response.jsonPath().getString("data.email"),
                expected.get("email"));
    }

    @Then("I receive error code {int} in response")
    public void iReceiveErrorCodeInResponse(int responseCode) {
        Assert.assertEquals(
                response.getStatusCode(),
                responseCode);
    }

    @Given("I create a user with following {word} {word}")
    public void iCreateUserWithFollowing(String sUsername, String sJob) {
        response = userClient.createUser(Map.of("name", sUsername, "job", sJob));
    }

    @Then("response should contain the following data")
    public void iReceiveErrorCodeInResponse(DataTable table) {
        table.asLists().get(0)
                .forEach(this::assertFieldPresent);

    }
    private void assertFieldPresent(String fieldName) {
        Object value = response.jsonPath().get(fieldName);
        Assert.assertNotNull(
                value,
                "Field '" + fieldName + "' is missing");

        if (value instanceof String) {
            Assert.assertFalse(
                    ((String) value).trim().isEmpty(),
                    "Field '" + fieldName + "' is empty");
        }
    }

    @Given("I login successfully with the following data")
    public void iLoginSuccesfullyWithFollowingData(DataTable dt) {
        loginWithData(dt);
    }

    private void loginWithData(DataTable dt) {
        Map<String, String> loginData = dt.asMaps(String.class, String.class).get(0);
        Map<String, String> normalizedLoginData = new HashMap<>();
        normalizedLoginData.put("email", loginData.getOrDefault("Email", loginData.getOrDefault("email", "")));
        normalizedLoginData.put("password", loginData.getOrDefault("Password", loginData.getOrDefault("password", "")));
        response = loginClient.login(normalizedLoginData);
    }

    @Given("I login unsuccessfully with the following data")
    public void iLoginUnsuccesfullyWithFollowingData(DataTable dt) {
        loginWithData(dt);
    }

    @Given("I wait for the user list to load")
    public void iWaitForUserListToLoad() {
        response = userClient.getUsersWithDelay(1, 3);
        allUserIds.clear();
        collectUserIds(response);
    }

    @Then("I should see that every user has a unique id")
    public void iShouldSeeThatEveryUserHasAUniqueID() {
        Assert.assertFalse(allUserIds.isEmpty(), "No user IDs were returned from the delayed users response.");
        Assert.assertEquals(
                new HashSet<>(allUserIds).size(),
                allUserIds.size(),
                "Expected every user ID to be unique in the delayed users response.");
    }

    @Then("I should get a response code of {int}")
    public void iShouldGetAResponseCodeOf(int responseCode) {

        Assert.assertEquals(
                response.getStatusCode(),
                responseCode,
                "Expected response code does not match actual.");
    }

    @And("I should see the following response message:")
    public void iShouldSeeTheFollowingResponseMessage(DataTable dt) {
        List<List<String>> rows = dt.asLists(String.class);
        if (rows.size() == 1 && rows.get(0).size() == 1) {
            Matcher matcher = Pattern.compile("\\\"([^\\\"]+)\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"")
                    .matcher(rows.get(0).get(0));
            Assert.assertTrue(matcher.find(), "Expected message format should be \"field\": \"value\"");
            Assert.assertEquals(
                    response.jsonPath().getString(matcher.group(1)),
                    matcher.group(2),
                    "Expected response message does not match actual.");
            return;
        }

        Map<String, String> expected = dt.asMaps(String.class, String.class).get(0);
        for (Map.Entry<String, String> entry : expected.entrySet()) {
            Assert.assertEquals(
                    response.jsonPath().getString(entry.getKey()),
                    entry.getValue(),
                    "Expected response field does not match actual for key: " + entry.getKey());
        }
    }

    // New steps for PUT/DELETE operations
    @Given("I update user {int} with following {word} {word}")
    public void iUpdateUserWithFollowing(int userId, String name, String job) {
        response = userClient.updateUser(userId, Map.of("name", name, "job", job));
    }

    @Given("I delete user {int}")
    public void iDeleteUser(int userId) {
        response = userClient.deleteUser(userId);
    }

    // New steps for header and response-time assertions
    @Then("the response header {string} should contain {string}")
    public void theResponseHeaderShouldContain(String headerName, String expectedValue) {
        String actualValue = response.getHeader(headerName);
        Assert.assertNotNull(
                actualValue,
                "Header '" + headerName + "' not found in response");
        Assert.assertTrue(
                actualValue.contains(expectedValue),
                "Expected header '" + headerName + "' to contain '" + expectedValue + "', but got: " + actualValue);
    }

    @And("the response time should be under {int} ms")
    public void theResponseTimeShouldBeUnderMs(long maxTimeInMs) {
        long responseTime = response.getTime();
        Assert.assertTrue(
                responseTime < maxTimeInMs,
                "Response time " + responseTime + "ms exceeded maximum allowed time " + maxTimeInMs + "ms");
    }
}
