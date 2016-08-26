package tests;

import org.junit.Test;
import tests.model.Customer;
import util.PenaltyOnTestFailure;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;


public class Hw2 extends AbstractHw {

    private final String BASE_URL = "http://localhost:8080/";

    @Test
    @PenaltyOnTestFailure(3)
    public void insertsFromForm() {
        postForm("customers/form", getForm("name", "Jack"));
        postForm("customers/form", getForm("name", "Jill"));

        List<Customer> customers = getList("api/customers");

        assertThat(customers.size(), greaterThanOrEqualTo(2));
        assertThat(getFirstNames(customers), hasItem("Jack"));
        assertThat(getFirstNames(customers), hasItem("Jill"));
    }

    @Test
    @PenaltyOnTestFailure(3)
    public void deletesAllCustomers() {
        postForm("customers/form", getForm("name", "Jack"));
        postForm("customers/form", getForm("name", "Jill"));

        delete("api/customers");

        assertThat(getList("api/customers"), is(empty()));
    }

    @Test
    @PenaltyOnTestFailure(3)
    public void insertFromJson() {

        delete("api/customers");

        postJson("api/customers", getCustomer("Jack", "Smith", "C1"));
        postJson("api/customers", getCustomer("Jane", "Smith", "C2"));

        List<Customer> customers = getList("api/customers");

        assertThat(customers.size(), is(2));
        assertThat(customers.get(0).getFirstName(), is("Jack"));
        assertThat(customers.get(1).getCode(), is("C2"));
    }

    private Form getForm(String name, String value) {
        Form form = new Form();
        form.param(name, value);
        return form;
    }

    private void postForm(String path, Form form) {
        getTarget()
            .path(path)
            .request()
            .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));
    }

    private List<String> getFirstNames(List<Customer> customers) {
        return customers.stream()
                .map(n -> n.getFirstName())
                .collect(Collectors.toList());
    }

    @Override
    protected String getBaseUrl() {
        return BASE_URL;
    }

}
