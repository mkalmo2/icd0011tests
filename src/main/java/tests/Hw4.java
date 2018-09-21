package tests;

import org.junit.Test;
import tests.model.Order;
import util.PenaltyOnTestFailure;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class Hw4 extends AbstractHw {

    private final String BASE_URL = "http://localhost:8080/";

    @Test
    @PenaltyOnTestFailure(10)
    public void baseUrlResponds() {
        boolean isSuccess = sendRequest(getBaseUrl());

        assertThat(isSuccess, is(true));
    }

    @Test
    @PenaltyOnTestFailure(10)
    public void canGetListOfAllOrders() {

        postOrder("api/orders", new Order("A123"));
        postOrder("api/orders", new Order("A456"));

        List<Order> allOrders = getList("api/orders");

        assertThat(allOrders.size(), is(greaterThanOrEqualTo(2)));

        List<String> allNumbers = allOrders.stream()
                .map(o -> o.getOrderNumber())
                .collect(Collectors.toList());

        assertThat(allNumbers, hasItems("A123", "A456"));
    }

    @Override
    protected String getBaseUrl() {
        return BASE_URL;
    }

}
