package api;


import config.LoadProp;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;

public class GenericApiClient {

    // Centralized Base URI handling with environment support
    private static final String BASE_URL = getBaseUrl();

    private static String getBaseUrl() {
        String env = System.getProperty("env", "live");
        if ("mock".equalsIgnoreCase(env)) {
            return LoadProp.getProperty("mock_api_url");
        }
        return LoadProp.getProperty("api_url");
    }

    protected RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .headers("x-api-key", LoadProp.getProperty("REQRES_API_KEY"))
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    // Generic GET
    public Response get(String endpoint, Map<String, String> queryParams) {
        RequestSpecification spec = getRequestSpec();
        if (queryParams != null) {
            spec.queryParams(queryParams);
        }
        return spec.when().get(endpoint);
    }

    // Generic POST
    public Response post(String endpoint, Object body) {
        return getRequestSpec()
                .body(body)
                .when()
                .post(endpoint);
    }

    // Generic PUT
    public Response put(String endpoint, Object body) {
        return getRequestSpec()
                .body(body)
                .when()
                .put(endpoint);
    }

    // Generic DELETE
    public Response delete(String endpoint) {
        return getRequestSpec()
                .when()
                .delete(endpoint);
    }
}

