package tests;

import org.junit.Test;
import tests.model.Customer;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;


public class Hw2 {

    private static final String BASE_URL = "http://localhost:8080/";
    private static final Client CLIENT = ClientBuilder.newClient();

    @Test
    public void insertsFromForm() {
        delete("customers");

        postForm("customers/form", getForm("name", "Jack"));
        postForm("customers/form", getForm("name", "Jill"));

        List<Customer> customers = get("customers");

        assertThat(customers.size(), is(2));
        assertThat(customers.get(0).getFirstName(), is("Jack"));
        assertThat(customers.get(1).getFirstName(), is("Jill"));
    }

    @Test
    public void deletesAllCustomers() {
        postForm("customers/form", getForm("name", "Jack"));
        postForm("customers/form", getForm("name", "Jill"));

        delete("customers");

        assertThat(get("customers"), is(empty()));
    }

    @Test
    public void insertFromJson() {

        delete("customers");

        postJson("customers", getCustomer("Jack", "Smith", "C1"));
        postJson("customers", getCustomer("Jane", "Smith", "C2"));

        List<Customer> customers = get("customers");

        assertThat(customers.size(), is(2));
        assertThat(customers.get(0).getFirstName(), is("Jack"));
        assertThat(customers.get(1).getCode(), is("C2"));
    }

    private Customer getCustomer(String firstName, String lastName, String code) {
        Customer customer = new Customer(null, firstName, lastName, null, code);
        customer.setPhones(null);
        return customer;
    }

    private WebTarget getTarget() {
        return CLIENT.target(BASE_URL);
    }

    private Form getForm(String name, String value) {
        Form form = new Form();
        form.param(name, value);
        return form;
    }

    public List<Customer> get(String path) {
        Response response = getTarget()
                .path(path)
                .request().get();
        return response.readEntity(new GenericType<List<Customer>>() {});
    }

    public void postJson(String path, Customer data) {
        getTarget()
                .path(path)
                .request()
                .post(Entity.entity(data, MediaType.APPLICATION_JSON));
    }

    public void postForm(String path, Form form) {
        getTarget()
                .path(path)
                .request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));
    }

    public void delete(String path) {
        getTarget().path(path).request().delete();
    }

}
