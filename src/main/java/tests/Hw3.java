package tests;

import org.junit.Test;
import tests.model.Customer;
import util.PenaltyOnTestFailure;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;


public class Hw3 extends AbstractHw {

    private final String BASE_URL = "http://localhost:8080/";

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

    @Test
    @PenaltyOnTestFailure(3)
    public void deletesAllCustomers() {
        postJson("api/customers", getCustomer("Jack", "Smith", "C1"));
        postJson("api/customers", getCustomer("Jane", "Smith", "C2"));

        delete("api/customers");

        assertThat(getList("api/customers"), is(empty()));
    }

    @Test
    @PenaltyOnTestFailure(3)
    public void getOneCustomer() {
        delete("api/customers");

        postJson("api/customers", getCustomer("Jack"));
        postJson("api/customers", getCustomer("Jane"));

        Long janeId = getList("api/customers").get(1).getId();

        Customer jane = getOne("api/customers", param("id", janeId));

        assertThat(jane.getFirstName(), is("Jane"));
    }

    @Test
    @PenaltyOnTestFailure(3)
    public void deleteOneCustomer() {
        delete("api/customers");

        postJson("api/customers", getCustomer("Jack"));
        postJson("api/customers", getCustomer("Jane"));

        Long janeId = getList("api/customers").get(1).getId();

        delete("api/customers", param("id", janeId));

        List<Customer> customers = getList("api/customers");

        assertThat(customers.size(), is(1));
        assertThat(customers.get(0).getFirstName(), is("Jack"));
    }

    @Override
    protected String getBaseUrl() {
        return BASE_URL;
    }

}
