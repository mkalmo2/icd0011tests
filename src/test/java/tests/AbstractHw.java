package tests;

import jakarta.ws.rs.client.*;
import org.junit.Rule;
import org.junit.rules.Timeout;
import tests.model.*;
import util.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assume.assumeTrue;

public abstract class AbstractHw {

    @Rule
    public Timeout globalTimeout = Timeout.seconds(20);

    protected abstract String getBaseUrl();

    private static boolean isLoggingEnabled = false;

    protected static String frameworkPathToSourceCode = "";

    protected static String arg1 = "";

    protected static void enableLogging() {
        isLoggingEnabled = true;
    }

    public static void setPathToSourceCode(String path) {
        frameworkPathToSourceCode = path;
    }

    public static void setArg1(String value) {
        arg1 = value;
    }

    protected void assumeProjectSourcePathIsSet(String path) {
        if (!frameworkPathToSourceCode.isEmpty()) {
            return;
        }

        assumeTrue("Please assign the path to your project source code directory " +
                        "to the field 'pathToProjectSourceCode'",
                path != null && !path.isEmpty());
    }

    protected void assertDoesNotContainString(List<FileReader.File> files,
                                            String targetString) {

        for (FileReader.File file : files) {
            String message = MessageFormat.format(
                    "file {0} contains string ''{1}''",
                    file.name, targetString);

            assertThat(message, file.contents, not(containsString(targetString)));
        }
    }

    protected List<FileReader.File> getProjectSource(String pathToProjectSourceCode) {
        Path path = resolveProjectSourcePath(pathToProjectSourceCode);

        return new FileReader().getAllFilesAndContentsFrom(path, List.of(".java"));
    }

    protected Path resolveProjectSourcePath(String pathToProjectSourceCode) {
        String path = frameworkPathToSourceCode.isEmpty()
                ? pathToProjectSourceCode
                : frameworkPathToSourceCode;

        return Paths.get(path);
    }

    private static Client getClient() {
        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[] {new NopX509TrustManager()}, new SecureRandom());
            return ClientBuilder.newBuilder()
                    .register(new LoggingFilter(isLoggingEnabled))
                    .register(JacksonConfig.class)
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
        return getList(path, new GenericType<List<Order>>() {}, parameters);
    }

    protected Order getOne(String path, Parameter... parameters) {
        return getOne(path, Order.class, parameters);
    }

    protected String getResponseAsString(String path) {
        return getOne(path, String.class);
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
        return postCommon(path, data, new GenericType<Order>() {});
    }

    protected Result<Order> postOrderFromJsonString(String path, String data) {
        return postCommon(path, data, new GenericType<Order>() {});
    }

    protected Result<Map<String, Object>> postMap(
            String path,
            Map<String, Object> data) {

        return postCommon(path, data, new GenericType<Map<String, Object>>() {});
    }

    protected boolean sendRequest(String path) {
        Response response = getClient()
                .target(path)
                .request()
                .header("x-is-base-url-request", "true")
                .get();

        return Response.Status.OK.getStatusCode() == response.getStatus();
    }

    protected void delete(String path, Parameter ... parameters) {
        WebTarget target = getTarget().path(path);

        for (Parameter p : parameters) {
            target = target.queryParam(p.getKey(), p.getValue());
        }

        closeQuietly(target.request().delete());
    }

    protected Order createOrder(String number, String ... items) {
        Order order = new Order(number);
        for (String item : items) {
            order.add(new OrderRow(item, 1, 100));
        }
        return order;
    }

    protected String postOrder(String url, String number, String ... items) {
        return postOrder(url, createOrder(number, items))
                .getValue()
                .getId();
    }

    protected void assertContainsItems(
            List<Order> orderList, String id, String ... itemName) {

        List<String> items = orderList.stream()
                .filter(o -> o.getId().equals(id))
                .flatMap(o -> o.getOrderRows().stream())
                .map(OrderRow::getItemName)
                .collect(Collectors.toList());

        assertThat(items, contains(itemName));
    }

    protected void assertHasIds(List<Order> orderList, String ... ids) {
        List<String> returnedOrderIds = orderList.stream()
                .map(Order::getId)
                .collect(Collectors.toList());

        assertThat(returnedOrderIds, hasItems(ids));
    }

    protected String getRandomString(int minLength, int maxLength) {
        return new SampleDataProvider(0).getRandomString(minLength, maxLength);
    }

    private <T> Result<T> readResult(Response response, GenericType<T> entityType) {
        Result<T> result = new Result<>();

        if (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            result.setErrors(response.readEntity(ValidationErrors.class).getErrors());
        } else {
            result.setValue(response.readEntity(entityType));
        }

        return result;
    }

    private <T> Result<T> postCommon(String path, Object data, GenericType<T> resultType) {
        Invocation.Builder request = getTarget()
                .path(path)
                .request(MediaType.APPLICATION_JSON);

        try (Response response = request.post(Entity.entity(data, MediaType.APPLICATION_JSON))) {
            return readResult(response, resultType);
        }
    }

    private <T> List<T> getList(String path,
                                GenericType<List<T>> type,
                                Parameter... parameters) {

        WebTarget target = getTarget().path(path);

        for (Parameter p : parameters) {
            target = target.queryParam(p.getKey(), p.getValue());
        }

        return target
                .request(MediaType.APPLICATION_JSON)
                .get(type);
    }

    private void closeQuietly(AutoCloseable closeable) {
        try (closeable) {

        } catch (Exception e) {
            // ignore
        }
    }

}
