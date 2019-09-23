package tests;

import org.junit.Test;
import tests.model.Order;
import tests.model.Result;
import util.IfThisTestFailsMaxPoints;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class Hw4 extends AbstractHw {

    private final String baseUrl = "http://localhost:8080/";

    @Test
    public void baseUrlResponds() {
        boolean isSuccess = sendRequest(getBaseUrl());

        assertThat(isSuccess, is(true));
    }

    @Test
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

    @Test
    @IfThisTestFailsMaxPoints(8)
    public void canGetOrderById() {
        Result<Order> result = postOrder("api/orders", new Order("A123"));

        assertThat(result.isSuccess(), is(true));

        String idOfPostedOrder = result.getValue().getId();

        Order order = getOne("api/orders", param("id", idOfPostedOrder));

        assertThat(order.getOrderNumber(), is("A123"));
        assertThat(order.getId(), is(idOfPostedOrder));
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }

}
