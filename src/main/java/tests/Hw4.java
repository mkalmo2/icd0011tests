package tests;

import org.junit.Test;
import tests.model.ClassifierInfo;
import tests.model.Customer;
import tests.model.Phone;

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
        ClassifierInfo info = getOne("classifiers", ClassifierInfo.class);
        assertThat(info.getCustomerTypes(),
                contains("customer_type.private", "customer_type.corporate"));
        assertThat(info.getPhoneTypes(),
                contains("phone_type.fixed", "phone_type.mobile"));
    }

    @Test
    @PenaltyOnTestFailure(5)
    public void getCustomersWithPhones() {
        String url = "api/customers";
        delete(url);

        Customer customer = new Customer();
        customer.setPhones(Arrays.asList(new Phone("mobile", "123"), new Phone("fixed", "456")));
        postJson(url, customer);

        customer = new Customer();
        customer.setPhones(Arrays.asList(new Phone("mobile", "789")));
        postJson(url, customer);

        customer = new Customer();
        customer.setPhones(Arrays.asList());
        postJson(url, customer);

        List<Customer> customers = getList(url);

        assertThat(customers.size(), is(3));

        List<Phone> phones = customers.get(0).getPhones();

        assertThat(phones.size(), is(2));
        assertThat(phones.get(0).getType(), is("mobile"));
        assertThat(phones.get(1).getValue(), is("456"));
    }

    @Test
    @PenaltyOnTestFailure(5)
    public void getCustomersWithPhones1() {
        delete("customers");

        Customer customer = new Customer();
        customer.setPhones(Arrays.asList(new Phone("mobile", "123"), new Phone("fixed", "456")));
        postJson("customers", customer);

        customer = new Customer();
        customer.setPhones(Arrays.asList(new Phone("mobile", "789")));
        postJson("customers", customer);

        customer = new Customer();
        customer.setPhones(Arrays.asList());
        postJson("customers", customer);

        List<Customer> customers = getList("customers");

        assertThat(customers.size(), is(3));

        List<Phone> phones = customers.get(0).getPhones();

        assertThat(phones.size(), is(2));
        assertThat(phones.get(0).getType(), is("mobile"));
        assertThat(phones.get(1).getValue(), is("456"));
    }

    @Test
    @PenaltyOnTestFailure(2)
    public void getOneCustomer() {
        new Hw3().getOneCustomer();
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

    @Test
    @PenaltyOnTestFailure(2)
    public void deleteOneCustomer() {
        new Hw3().deleteOneCustomer();
    }

    @Override
    protected String getBaseUrl() {
        return BASE_URL;
    }
}
