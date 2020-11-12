package tests;

import org.glassfish.jersey.client.ClientProperties;
import org.hamcrest.core.AnyOf;
import org.junit.Test;
import tests.model.LoginData;
import util.RequestResult;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


public class Hw10 extends AbstractHw {

    private final String baseUrl = "http://localhost:8080/";

    @Test
    public void versionInfoIsNotProtected() {
        RequestResult result = getRequest("/api/version");

        assertThat(result.getStatusCode(), is(200));
    }

    @Test
    public void canNotAccessProtectedResourceWithoutLoggingIn() {
        RequestResult result = getRequest("api/orders");

        assertThat(result.getStatusCode(), is(authFailureCode()));
    }

    @Test
    public void canAccessProtectedResourceWhenLoggedIn() {
        String token = loginWith("user", "user");

        RequestResult result = getRequest("api/orders", token);

        assertThat(result.getStatusCode(), is(200));
    }

    @Test
    public void userCanAccessOnlyOwnInfo() {
        String token = loginWith("user", "user");

        assertThat(getRequest("api/users", token).getStatusCode(),
                is(authFailureCode()));
        assertThat(getRequest("api/users/jill", token).getStatusCode(),
                is(authFailureCode()));
        assertThat(getRequest("api/users/user", token).getStatusCode(), is(200));
    }

    @Test
    public void adminCanAccessAllUsersInfo() {
        String token = loginWith("admin", "admin");

        assertThat(getRequest("api/users", token).getStatusCode(), is(200));
        assertThat(getRequest("api/users/user", token).getStatusCode(), is(200));
    }

    private String loginWith(String userName, String password) {
        RequestResult requestResult = postJson("api/login",
                new LoginData(userName, password));

        var token = requestResult.getAuthorization();

        assertThat(token, is(notNullValue()));

        return token;
    }

    private RequestResult postJson(String path, LoginData data) {
        Response response = getTarget()
                .path(path)
                .request()
                .post(Entity.entity(data, MediaType.APPLICATION_JSON));

        return new RequestResult()
                .withStatusCode(response.getStatus())
                .withAuthorization(response.getHeaderString("Authorization"));
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

    private AnyOf<Integer> authFailureCode() {
        return anyOf(equalTo(401), equalTo(403));
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }
}
