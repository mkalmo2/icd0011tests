package tests;

import org.junit.Test;
import tests.model.Order;
import tests.model.Result;
import util.SampleDataProvider;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class Hw05 extends AbstractHw {

    private final String baseUrl = "http://localhost:8080/";

    @Test
    public void baseUrlResponds() {
        boolean isSuccess = sendRequest(getBaseUrl());

        assertThat(isSuccess, is(true));
    }

    @Test
    public void canGetListOfAllOrders() {

        String orderNumber1 = getRandomString();
        String orderNumber2 = orderNumber1 + "x";

        postOrder("api/orders", new Order(orderNumber1));
        postOrder("api/orders", new Order(orderNumber2));

        List<Order> allOrders = getList("api/orders");

        assertThat(allOrders.size(), is(greaterThanOrEqualTo(2)));

        List<String> allNumbers = allOrders.stream()
                .map(Order::getOrderNumber)
                .collect(Collectors.toList());

        assertThat(allNumbers, hasItems(orderNumber1, orderNumber2));
    }

    @Test
    public void canGetOrderById() {
        String orderNumber = getRandomString();

        Result<Order> result = postOrder("api/orders", new Order(orderNumber));

        assertThat(result.isSuccess(), is(true));

        String idOfPostedOrder = result.getValue().getId();

        Order order = getOne("api/orders", param("id", idOfPostedOrder));

        assertThat(order.getOrderNumber(), is(orderNumber));
        assertThat(order.getId(), is(idOfPostedOrder));
    }

    private String getRandomString() {
        return new SampleDataProvider(0).getRandomString(3, 5);
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }
}
