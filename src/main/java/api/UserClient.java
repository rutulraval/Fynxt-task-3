package api;

import config.LoadProp;
import io.restassured.response.Response;
import java.util.Map;

public class UserClient extends GenericApiClient {

    private final String USERS_ENDPOINT = LoadProp.getProperty("usersEndpoint");

    public Response getUser(int userId) {
        return get(USERS_ENDPOINT+"/" + userId, null);
    }
    public Response getUsers(int page) {
        return get(USERS_ENDPOINT, Map.of("page", String.valueOf(page)));
    }

    public Response getUsersWithDelay(int page, int delayInSeconds) {
        return get(
                USERS_ENDPOINT,
                Map.of(
                        "page", String.valueOf(page),
                        "delay", String.valueOf(delayInSeconds)));
    }

    public Response createUser(Object userModel) {
        return post(USERS_ENDPOINT, userModel);
    }

    public Response updateUser(int userId, Object userModel) {
        return put(USERS_ENDPOINT + "/" + userId, userModel);
    }

    public Response deleteUser(int userId) {
        return delete(USERS_ENDPOINT + "/" + userId);
    }
}

