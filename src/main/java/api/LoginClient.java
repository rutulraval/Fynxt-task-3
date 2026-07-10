package api;
import config.LoadProp;
import io.restassured.response.Response;

public class LoginClient extends GenericApiClient {
    private final String LOGIN_ENDPOINT = LoadProp.getProperty("loginEndpoint");
    public Response login(Object loginModel) {
        return post(LOGIN_ENDPOINT, loginModel);
    }
}
