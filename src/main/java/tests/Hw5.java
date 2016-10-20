package tests;

import org.junit.Test;
import tests.model.Customer;
import tests.model.ValidationError;
import util.PenaltyOnTestFailure;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;


public class Hw5 extends AbstractHw {

    private final String BASE_URL = "http://localhost:8080/";

    @Test
    @PenaltyOnTestFailure(4)
    public void postingInvalidCustomerDataReturnErrorsCodes() {
        List<ValidationError> errors = postJson("api/customers",
                getCustomer("", null, "*bad_code*"));

        List<String> codes = getCodes(errors);

        assertThat(codes, hasItem(startsWith("NotNull")));
        assertThat(codes, hasItem(startsWith("Size")));
        assertThat(codes, hasItem(startsWith("Pattern")));
    }

    @Test
    @PenaltyOnTestFailure(4)
    public void wideCardSearchFiltersResults() {
        delete("api/customers");

        postJson("api/customers", getCustomer("Mari", "Kuusk", "C1"));
        postJson("api/customers", getCustomer("Jaak", "Varres", "C2"));
        postJson("api/customers", getCustomer("Tiina", "Kask", "AR2"));
        postJson("api/customers", getCustomer("Mati", "Tamm", "C3"));

        List<Customer> filtered = getList("api/customers/search", param("key", "ar"));

        assertThat(getFirstNames(filtered), contains("Mari", "Jaak", "Tiina"));
    }

    @Test
    @PenaltyOnTestFailure(4)
    public void canGetOneCustomer() {
        delete("api/customers");

        postJson("api/customers", getCustomer("Jane", "Smith", "C1"));

        Long janeId = getList("api/customers").get(0).getId();

        Customer jane = getOne("api/customers/" + janeId);

        assertThat(jane.getFirstName(), is("Jane"));
    }

    @Test
    @PenaltyOnTestFailure(4)
    public void canDeleteOneCustomer() {
        delete("api/customers");

        postJson("api/customers", getCustomer("Jane", "Smith", "C1"));
        postJson("api/customers", getCustomer("Jack", "Smith", "C2"));

        Long janeId = getList("api/customers").get(0).getId();

        delete("api/customers/" + janeId);

        List<Customer> customers = getList("api/customers");

        assertThat(customers.size(), is(1));
        assertThat(customers.get(0).getFirstName(), is("Jack"));
    }

    private List<String> getFirstNames(List<Customer> customers) {
        return customers.stream()
                .map(n -> n.getFirstName())
                .collect(Collectors.toList());
    }

    private List<String> getCodes(List<ValidationError> errors) {
        return errors.stream()
                .map(n -> n.getCode())
                .collect(Collectors.toList());
    }

    @Override
    protected String getBaseUrl() {
        return BASE_URL;
    }
}
