package tests;

import org.junit.jupiter.api.Test;
import tests.model.Order;
import tests.model.Result;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Hw05 extends AbstractHw {

    private final String baseUrl = "http://localhost:8080/";

    @Test
    public void baseUrlResponds() {
        boolean isSuccess = sendRequest(getBaseUrl());

        assertThat(isSuccess).isTrue();
    }

    @Test
    public void canGetListOfAllOrders() {

        String orderNumber1 = getRandomString(3, 5);
        String orderNumber2 = orderNumber1 + "x";

        postOrder("api/orders", new Order(orderNumber1));
        postOrder("api/orders", new Order(orderNumber2));

        List<Order> allOrders = getList("api/orders");

        assertThat(allOrders).hasSizeGreaterThanOrEqualTo(2);

        List<String> allNumbers = allOrders.stream()
                .map(Order::getOrderNumber)
                .toList();

        assertThat(allNumbers).contains(orderNumber1, orderNumber2);
    }

    @Test
    public void canGetOrderById() {
        String orderNumber = getRandomString(3, 5);

        Result<Order> result = postOrder("api/orders", new Order(orderNumber));

        assertThat(result.isSuccess()).isTrue();

        String idOfPostedOrder = result.getValue().getId();

        Order order = getOne("api/orders", param("id", idOfPostedOrder));

        assertThat(order.getOrderNumber()).isEqualTo(orderNumber);
        assertThat(order.getId()).isEqualTo(idOfPostedOrder);
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }
}