package tests;

import org.junit.Test;
import tests.model.ClassifierInfo;
import tests.model.Customer;
import tests.model.Phone;
import util.PenaltyOnTestFailure;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;


public class Hw4 extends AbstractHw {

    private final String BASE_URL = "http://localhost:8080/";

    @Test
    @PenaltyOnTestFailure(5)
    public void providesClassifierInfo() {
        ClassifierInfo info = getOne("api/classifiers", ClassifierInfo.class);
        assertThat(info.getCustomerTypes(),
                contains("customer_type.private", "customer_type.corporate"));
        assertThat(info.getPhoneTypes(),
                contains("phone_type.fixed", "phone_type.mobile"));
    }

    @Test
    @PenaltyOnTestFailure(2)
    public void customerHasCustomerType() {
        delete("api/customers");

        Customer customer = new Customer();
        customer.setType("customer_type.corporate");

        postJson("api/customers", customer);

        List<Customer> customers = getList("api/customers");

        assertThat(customers.get(0).getType(), is("customer_type.corporate"));
    }

    @Test
    @PenaltyOnTestFailure(5)
    public void getCustomersWithPhones() {
        delete("api/customers");

        Customer customer = new Customer();
        customer.setPhones(Arrays.asList(new Phone("phone_type.mobile", "123"),
                                         new Phone("phone_type.fixed", "456")));
        postJson("api/customers", customer);

        customer = new Customer();
        customer.setPhones(Arrays.asList(new Phone("phone_type.mobile", "789")));
        postJson("api/customers", customer);

        customer = new Customer();
        customer.setPhones(Arrays.asList());
        postJson("api/customers", customer);

        List<Customer> customers = getList("api/customers");

        assertThat(customers.size(), is(3));

        List<Phone> phones = customers.get(0).getPhones();

        assertThat(phones.size(), is(2));
        assertThat(phones.get(0).getType(), is("phone_type.mobile"));
        assertThat(phones.get(1).getValue(), is("456"));
    }

    @Override
    protected String getBaseUrl() {
        return BASE_URL;
    }
}
