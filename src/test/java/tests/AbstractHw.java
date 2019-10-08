package tests;

import org.junit.Rule;
import org.junit.rules.Timeout;
import tests.model.Order;
import tests.model.Result;
import tests.model.ValidationErrors;
import util.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;

public abstract class AbstractHw {

    @Rule
    public Timeout globalTimeout = Timeout.seconds(10);

    protected abstract String getBaseUrl();

    private static boolean isDebug = false;

    protected static String frameworkPathToSourceCode = "";

    protected static void setDebug(boolean debug) {
        isDebug = debug;
    }

    public static void setPathToSourceCode(String path) {
        frameworkPathToSourceCode = path;
    }

    protected void assertProjectSourcePathIsSet(String path) {
        if (!frameworkPathToSourceCode.isEmpty()) {
            return;
        }

        assertFalse("Please assign the path to your project source code directory " +
                        "to the field 'pathToProjectSourceCode'",
                path == null || path.isEmpty());
    }

    protected void assertDoesNotContainString(List<JavaFileReader.File> files,
                                            String targetString) {

        for (JavaFileReader.File file : files) {
            String message = MessageFormat.format(
                    "file {0} contains string ''{1}''",
                    file.name, targetString);

            assertThat(message, file.contents, not(containsString(targetString)));
        }
    }

    protected List<JavaFileReader.File> getProjectSource(String pathToProjectSourceCode) {
        String path = frameworkPathToSourceCode.isEmpty()
                ? pathToProjectSourceCode
                : frameworkPathToSourceCode;

        return new JavaFileReader().getAllFilesFrom(Paths.get(path));
    }

    private static Client getClient() {
        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[] {new NopX509TrustManager()}, new SecureRandom());
            return ClientBuilder.newBuilder()
                    .register(new LoggingFilter(isDebug))
                    .register(ContentTypeFilter.class)
                    .sslContext(sslcontext).hostnameVerifier((s1, s2) -> true).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public WebTarget getTarget() {
        return getClient().target(getBaseUrl());
    }

    protected List<Order> getList(String path, Parameter... parameters) {
        WebTarget target = getTarget().path(path);

        for (Parameter p : parameters) {
            target = target.queryParam(p.getKey(), p.getValue());
        }

        return target
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Order>>() {});
    }

    protected Order getOne(String path, Parameter... parameters) {
        return getOne(path, Order.class, parameters);
    }

    protected <T> T getOne(String path, Class<T> clazz, Parameter ... parameters) {
        WebTarget target = getTarget().path(path);

        for (Parameter p : parameters) {
            target = target.queryParam(p.getKey(), p.getValue());
        }

        return target
                .request(MediaType.APPLICATION_JSON)
                .get(clazz);
    }

    protected Parameter param(String key, Object value) {
        return new Parameter(key, String.valueOf(value));
    }

    protected Result<Order> postOrder(String path, Order data) {
        Response response = getTarget()
                .path(path)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(data, MediaType.APPLICATION_JSON));

        Result<Order> result = new Result<>();

        if (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            result.setErrors(response.readEntity(ValidationErrors.class).getErrors());
        } else {
            result.setValue(response.readEntity(Order.class));
        }

        return result;
    }

    protected Result<Map<String, String>> postMap(String path, Map<String, String> data) {
        Response response = getTarget()
                .path(path)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(data, MediaType.APPLICATION_JSON));

        Result<Map<String, String>> result = new Result<>();

        if (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            result.setErrors(response.readEntity(ValidationErrors.class).getErrors());
        } else {
            result.setValue(response.readEntity(
                    new GenericType<Map<String, String>>() {}));
        }

        return result;
    }

    protected boolean sendRequest(String path) {
        Response response = getClient()
                .target(path)
                .request()
                .header("x-is-base-url-request", "true")
                .get();

        return Response.Status.OK.getStatusCode() == response.getStatus();
    }

    protected Result<Order> postOrderFromJsonString(String path, String data) {
        Response response = getTarget()
                .path(path)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(data, MediaType.APPLICATION_JSON));

        Result<Order> result = new Result<>();

        if (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            result.setErrors(response.readEntity(ValidationErrors.class).getErrors());
        } else {
            result.setValue(response.readEntity(Order.class));
        }

        return result;
    }

    protected void delete(String path, Parameter ... parameters) {
        WebTarget target = getTarget().path(path);

        for (Parameter p : parameters) {
            target = target.queryParam(p.getKey(), p.getValue());
        }

        target.request().delete();
    }

}
