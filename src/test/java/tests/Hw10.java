package tests;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.jupiter.api.Test;
import tests.model.LoginData;
import util.RequestResult;

import static org.assertj.core.api.Assertions.assertThat;


public class Hw10 extends AbstractHw {

    private final String baseUrl = "http://localhost:8080/";

    @Test
    public void versionInfoIsNotProtected() {
        RequestResult result = getRequest("/api/version");

        assertThat(result.getStatusCode()).isEqualTo(200);
    }

    @Test
    public void canNotAccessProtectedResourceWithoutLoggingIn() {
        RequestResult result = getRequest("api/orders");

        assertThat(result.getStatusCode()).isIn(401, 403);
    }

    @Test
    public void canAccessProtectedResourceWhenLoggedIn() {
        String token = loginWith("user", "user");

        RequestResult result = getRequest("api/orders", token);

        assertThat(result.getStatusCode()).isEqualTo(200);
    }

    @Test
    public void userCanAccessOnlyOwnInfo() {
        String token = loginWith("user", "user");

        assertThat(getRequest("api/users", token).getStatusCode())
                .isIn(401, 403);
        assertThat(getRequest("api/users/jill", token).getStatusCode())
                .isIn(401, 403);
        assertThat(getRequest("api/users/user", token).getStatusCode()).isEqualTo(200);
    }

    @Test
    public void adminCanAccessAllUsersInfo() {
        String token = loginWith("admin", "admin");

        assertThat(getRequest("api/users", token).getStatusCode()).isEqualTo(200);
        assertThat(getRequest("api/users/user", token).getStatusCode()).isEqualTo(200);
    }

    private String loginWith(String userName, String password) {
        RequestResult requestResult = postJson("api/login",
                new LoginData(userName, password));

        var token = requestResult.getAuthorization();

        assertThat(token).isNotNull();

        return token;
    }

    private RequestResult postJson(String path, LoginData data) {
        Invocation.Builder request = getTarget()
                .path(path)
                .request(MediaType.APPLICATION_JSON);

        try (Response response = request.post(Entity.entity(data, MediaType.APPLICATION_JSON))) {
            return new RequestResult()
                .withStatusCode(response.getStatus())
                .withAuthorization(response.getHeaderString("Authorization"));
        }
    }

    private RequestResult getRequest(String path) {
        return getRequest(path, null);
    }

    private RequestResult getRequest(String path, String token) {
        WebTarget target = getTarget()
                .property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE)
                .path(path);

        Invocation.Builder request = target.request();

        request.header("Authorization", token);

        Response response = request.get();

        return new RequestResult()
                .withContents(response.readEntity(String.class))
                .withStatusCode(response.getStatus());
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }
}
