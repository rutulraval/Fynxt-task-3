package api;

import api.reporting.ApiExtentTestListener;
import api.reporting.ApiReportContext;
import config.LoadProp;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import util.CsvDataReader;

import java.util.List;
import java.util.Map;

@Listeners(ApiExtentTestListener.class)
public class CreateUserDataDrivenTest {

    private final UserClient userClient = new UserClient();

    /**
     * DataProvider that reads CSV file and provides test data
     */
    @DataProvider(name = "userDataFromCSV")
    public Object[][] getUserDataFromCSV() {
        List<Map<String, String>> csvData = CsvDataReader.readCsvFile(LoadProp.getProperty("csvDataFile"));
        Object[][] data = new Object[csvData.size()][2];

        for (int i = 0; i < csvData.size(); i++) {
            Map<String, String> row = csvData.get(i);
            data[i][0] = row.get("name");
            data[i][1] = row.get("job");
        }

        return data;
    }

    /**
     * Test method that creates users using data from CSV
     */
    @Test(dataProvider = "userDataFromCSV", description = "Create ReqRes users from CSV data using the pure TestNG API flow")
    public void testCreateUserWithCsvData(String name, String job) {
        Map<String, String> userData = Map.of("name", name, "job", job);
        Response response = userClient.createUser(userData);

        ApiReportContext.record("POST", "/api/users", userData, response);

        Assert.assertEquals(response.getStatusCode(), 201,
                "Expected status code 201 but got " + response.getStatusCode());
        Assert.assertTrue(response.getContentType().contains("application/json"),
                "Expected JSON response but got " + response.getContentType());
        Assert.assertTrue(response.getTime() < 5000,
                "Expected response time under 5000ms but got " + response.getTime());
        Assert.assertEquals(response.jsonPath().getString("name"), name,
                "Expected name " + name + " but got " + response.jsonPath().getString("name"));
        Assert.assertEquals(response.jsonPath().getString("job"), job,
                "Expected job " + job + " but got " + response.jsonPath().getString("job"));
        Assert.assertNotNull(response.jsonPath().getString("id"),
                "Expected created user response to contain an id");

        System.out.println("Successfully created user: " + name + " with job: " + job);
    }
}

