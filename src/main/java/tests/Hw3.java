package tests;

import org.junit.Test;
import tests.model.Customer;
import util.PenaltyOnTestFailure;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;


public class Hw3 extends AbstractHw {

    private final String BASE_URL = "http://localhost:8080/";

    @Test
    @PenaltyOnTestFailure(3)
    public void insertFromJson() {
        new Hw2().insertFromJson();
    }

    @Test
    @PenaltyOnTestFailure(3)
    public void deletesAllCustomers() {
        new Hw2().deletesAllCustomers();
    }

    @Test
    @PenaltyOnTestFailure(3)
    public void getOneCustomer() {
        delete("customers");

        postJson("customers", getCustomer("Jack"));
        postJson("customers", getCustomer("Jane"));

        Long janeId = getList("customers").get(1).getId();

        Customer jane = getOne("customers", param("id", janeId));

        assertThat(jane.getFirstName(), is("Jane"));
    }

    @Test
    @PenaltyOnTestFailure(3)
    public void deleteOneCustomer() {
        delete("customers");

        postJson("customers", getCustomer("Jack"));
        postJson("customers", getCustomer("Jane"));

        Long janeId = getList("customers").get(1).getId();

        delete("customers", param("id", janeId));

        List<Customer> customers = getList("customers");

        assertThat(customers.size(), is(1));
        assertThat(customers.get(0).getFirstName(), is("Jack"));
    }

    @Override
    protected String getBaseUrl() {
        return BASE_URL;
    }
}
