package tests;

import org.junit.Test;
import tests.model.Customer;
import util.PenaltyOnTestFailure;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


public class Hw5 extends AbstractHw {

    private final String BASE_URL = "http://localhost:8080/";

    @Test
    @PenaltyOnTestFailure(4)
    public void getOneCustomer() {
        delete("api/customers");

        postJson("api/customers", getCustomer("Jack", "Smith", "A1"));
        postJson("api/customers", getCustomer("Jane", "Smith", "A2"));

        Long janeId = getList("api/customers").get(1).getId();

        Customer jane = getOne("api/customers/" + janeId);

        assertThat(jane.getFirstName(), is("Jane"));
    }

    @Test
    @PenaltyOnTestFailure(4)
    public void deleteOneCustomer() {
        delete("customers");

        postJson("customers", getCustomer("Jack"));
        postJson("customers", getCustomer("Jane"));

        Long janeId = getList("customers").get(1).getId();

        delete("customers/" + janeId);

        List<Customer> customers = getList("customers");

        assertThat(customers.size(), is(1));
        assertThat(customers.get(0).getFirstName(), is("Jack"));
    }

    @Test
    @PenaltyOnTestFailure(2)
    public void insertFromJson() {
        new Hw2().insertFromJson();
    }

    @Test
    @PenaltyOnTestFailure(2)
    public void deletesAllCustomers() {
        new Hw2().deletesAllCustomers();
    }

    @Override
    protected String getBaseUrl() {
        return BASE_URL;
    }
}
