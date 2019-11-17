package tests;

import org.glassfish.jersey.client.ClientProperties;
import org.hamcrest.core.AnyOf;
import org.junit.Test;
import tests.model.LoginData;
import util.IfThisTestFailsMaxPoints;
import util.RequestResult;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class Hw9 extends AbstractHw {

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
        Optional<String> token = loginWith("user", "user");

        RequestResult result = getRequest("api/orders", token);

        assertThat(result.getStatusCode(), is(200));
    }

    @Test
    @IfThisTestFailsMaxPoints(8)
    public void userCanAccessOnlyOwnInfo() {
        Optional<String> token = loginWith("user", "user");

        assertThat(getRequest("api/users", token).getStatusCode(),
                is(authFailureCode()));
        assertThat(getRequest("api/users/jill", token).getStatusCode(),
                is(authFailureCode()));
        assertThat(getRequest("api/users/user", token).getStatusCode(), is(200));
    }

    @Test
    @IfThisTestFailsMaxPoints(8)
    public void adminCanAccessAllUsersInfo() {
        Optional<String> token = loginWith("admin", "admin");

        assertThat(getRequest("api/users", token).getStatusCode(), is(200));
        assertThat(getRequest("api/users/user", token).getStatusCode(), is(200));
    }

    private Optional<String> loginWith(String userName, String password) {
        RequestResult requestResult = postJson("api/login",
                new LoginData(userName, password));

        return requestResult.getAuthorization();
    }

    private RequestResult postJson(String path, LoginData data) {
        Response response = getTarget()
                .path(path)
                .request()
                .post(Entity.entity(data, MediaType.APPLICATION_JSON));

        RequestResult result = new RequestResult();
        result.setStatusCode(response.getStatus());
        result.setAuthorization(
                Optional.ofNullable(response.getHeaderString("Authorization")));

        return result;
    }

    private RequestResult getRequest(String path) {
        return getRequest(path, Optional.empty());
    }

    private RequestResult getRequest(String path, Optional<String> token) {
        WebTarget target = getTarget()
                .property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE)
                .path(path);

        Invocation.Builder request = target.request();

        token.ifPresent(c -> request.header("Authorization", token.get()));

        Response response = request.get();

        RequestResult result = new RequestResult();
        result.setStatusCode(response.getStatus());

        return result;
    }

    private AnyOf<Integer> authFailureCode() {
        return anyOf(equalTo(401), equalTo(403));
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }
}
