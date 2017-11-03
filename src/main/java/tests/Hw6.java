package tests;

import org.hamcrest.core.AnyOf;
import org.junit.Test;
import util.LoginData;
import util.PenaltyOnTestFailure;
import util.RequestResult;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class Hw6 extends AbstractHw {

    private final String BASE_URL = "http://localhost:8080/";

    @Test
    @PenaltyOnTestFailure(10)
    public void classifierInfoIsNotProtected() {
        RequestResult result = getRequest("api/classifiers");

        assertThat(result.getStatusCode(), is(200));
    }

    @Test
    @PenaltyOnTestFailure(10)
    public void accessingProtectedResourceWithoutLoggingInGivesAuthFailureCode() {
        RequestResult result = getRequest("api/customers");

        assertThat(result.getStatusCode(), is(authFailureCode()));
    }

    @Test
    @PenaltyOnTestFailure(10)
    public void accessingProtectedResourceWhenLoggedInSucceeds() {
        RequestResult result = postJson("api/login", new LoginData("user", "user"));

        result = getRequest("api/customers", result.getCookie());

        assertThat(result.getStatusCode(), is(200));
    }

    @Test
    @PenaltyOnTestFailure(2)
    public void logoutInvalidatesSession() {
        Optional<Cookie> cookie = loginWith("user", "user");

        getRequest("api/logout", cookie);

        assertThat(getRequest("api/customers", cookie).getStatusCode(),
                is(authFailureCode()));
    }

    @Test
    @PenaltyOnTestFailure(2)
    public void userCanAccessOnlyOwnInfo() {
        Optional<Cookie> cookie = loginWith("user", "user");

        assertThat(getRequest("api/users", cookie).getStatusCode(),
                is(authFailureCode()));
        assertThat(getRequest("api/users/jill", cookie).getStatusCode(),
                is(authFailureCode()));
        assertThat(getRequest("api/users/user", cookie).getStatusCode(), is(200));
    }

    private AnyOf<Integer> authFailureCode() {
        return anyOf(equalTo(401), equalTo(403));
    }

    @Test
    @PenaltyOnTestFailure(2)
    public void adminCanAccessAllUsersInfo() {
        Optional<Cookie> cookie = loginWith("admin", "admin");

        assertThat(getRequest("api/users", cookie).getStatusCode(), is(200));
        assertThat(getRequest("api/users/user", cookie).getStatusCode(), is(200));
    }

    private Optional<Cookie> loginWith(String userName, String password) {
        return postJson("api/login", new LoginData(userName, password)).getCookie();
    }

    private RequestResult postJson(String path, LoginData data) {
        Response response = getTarget()
                .path(path)
                .request()
                .post(Entity.entity(data, MediaType.APPLICATION_JSON));

        RequestResult result = new RequestResult();
        result.setStatusCode(response.getStatus());
        result.setCookie(Optional.ofNullable(response.getCookies().get("JSESSIONID")));

        return result;
    }

    private RequestResult getRequest(String path) {
        return getRequest(path, Optional.empty());
    }

    private RequestResult getRequest(String path, Optional<Cookie> cookie) {
        WebTarget target = getTarget().path(path);

        Invocation.Builder request = target.request(MediaType.APPLICATION_JSON);

        cookie.ifPresent(c -> request.cookie(c));

        Response response = request.get();

        RequestResult result = new RequestResult();
        result.setStatusCode(response.getStatus());
        result.setCookie(Optional.ofNullable(response.getCookies().get("JSESSIONID")));

        return result;
    }

    @Override
    protected String getBaseUrl() {
        return BASE_URL;
    }
}
