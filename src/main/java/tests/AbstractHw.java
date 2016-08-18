package tests;

import lombok.AllArgsConstructor;
import lombok.ToString;
import tests.model.Customer;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.List;

public abstract class AbstractHw {

    protected abstract String getBaseUrl();

    protected static final Client client = ClientBuilder.newClient();

    public WebTarget getTarget() {
        return client.target(getBaseUrl());
    }

    protected Customer getCustomer(String firstName, String lastName, String code) {
        return new Customer(null, firstName, lastName, null, code);
    }

    protected Customer getCustomer(String firstName) {
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        return customer;
    }

    protected List<Customer> getList(String path) {
        return getTarget()
                .path(path)
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Customer>>() {});
    }

    protected Customer getOne(String path, Parameter ... parameters) {
        return getOne(path, Customer.class, parameters);
    }

    protected <T> T getOne(String path, Class<T> clazz, Parameter ... parameters) {
        WebTarget target = getTarget().path(path);

        for (Parameter p : parameters) {
            target = target.queryParam(p.key, p.value);
        }

        return target
                .request(MediaType.APPLICATION_JSON)
                .get(clazz);
    }

    protected Parameter param(String key, Object value) {
        return new Parameter(key, String.valueOf(value));
    }

    @ToString
    @AllArgsConstructor
    public static class Parameter {
        private String key;
        private String value;
    }

    protected void postJson(String path, Customer data) {
        getTarget()
            .path(path)
            .request()
            .post(Entity.entity(data, MediaType.APPLICATION_JSON));
    }

    protected void delete(String path, Parameter ... parameters) {
        WebTarget target = getTarget().path(path);

        for (Parameter p : parameters) {
            target = target.queryParam(p.key, p.value);
        }

        target.request().delete();
    }
}
