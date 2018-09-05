package tests;

import tests.model.*;
import util.LoggingFilter;
import util.NopX509TrustManager;
import util.Parameter;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class AbstractHw {

    protected abstract String getBaseUrl();

    private static boolean isDebug = false;

    protected static void setDebug(boolean debug) {
        isDebug = debug;
    }

    private static Client getClient() {
        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[] {new NopX509TrustManager()}, new SecureRandom());
            return ClientBuilder.newBuilder()
                    .register(new LoggingFilter(isDebug))
                    .sslContext(sslcontext).hostnameVerifier((s1, s2) -> true).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public WebTarget getTarget() {
        return getClient().target(getBaseUrl());
    }

    protected Customer getCustomer(String firstName, String lastName, String code) {
        return new Customer(null, firstName, lastName, null, code);
    }

    protected Customer getCustomer(String firstName) {
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        return customer;
    }

    protected List<Customer> getList(String path, Parameter... parameters) {
        WebTarget target = getTarget().path(path);

        for (Parameter p : parameters) {
            target = target.queryParam(p.getKey(), p.getValue());
        }

        return target
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Customer>>() {});
    }

    protected Customer getOne(String path, Parameter... parameters) {
        return getOne(path, Customer.class, parameters);
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
                .request()
                .post(Entity.entity(data, MediaType.APPLICATION_JSON));

        Result<Order> result = new Result<>();

        if (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            result.setErrors(response.readEntity(ValidationErrors.class).getErrors());
        } else {
            result.setValue(response.readEntity(Order.class));
        }

        return result;
    }

    protected boolean sendRequest(String path) {
        Response response = getClient()
                .target(path)
                .request()
                .get();

        return Response.Status.OK.getStatusCode() == response.getStatus();
    }

    protected Result<Order> postOrderFromJsonString(String path, String data) {
        Response response = getTarget()
                .path(path)
                .request()
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
