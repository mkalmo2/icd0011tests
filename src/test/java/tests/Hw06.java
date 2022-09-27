package tests;

import org.junit.Test;
import tests.model.Order;
import tests.model.OrderRow;
import tests.model.Result;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


public class Hw06 extends AbstractHw {

    private final String baseUrl = "http://localhost:8080/";

    @Test
    public void baseUrlResponds() {
        boolean isSuccess = sendRequest(getBaseUrl());

        assertThat(isSuccess, is(true));
    }

    @Test
    public void returnsPostedOrderWithAddedId() {
        Order order = new Order("A123");

        Result<Order> result = postOrder("api/orders", order);

        String idOfPostedOrder = result.getValue().getId();

        assertThat(idOfPostedOrder, is(notNullValue()));
    }

    @Test
    public void addsOrderWithOrderRows() {
        Order order = new Order("A456");
        order.add(new OrderRow("CPU", 2, 100));
        order.add(new OrderRow("Motherboard", 3, 60));

        Result<Order> result = postOrder("api/orders", order);

        String idOfPostedOrder = result.getValue().getId();

        Order read = getOne("api/orders", param("id", idOfPostedOrder));

        assertThat(read.getOrderRows().size(), is(2));
        assertThat(read.getOrderRows().get(1).getItemName(), is("Motherboard"));
    }

    @Test
    public void deletesOrderById() {

        Result<Order> result = postOrder("api/orders", new Order("A1"));

        String idOfPostedOrder = result.getValue().getId();

        delete("api/orders", param("id", idOfPostedOrder));

        List<Order> allOrders = getList("api/orders");

        List<String> allIds = allOrders.stream()
                .map(Order::getId)
                .toList();

        assertThat(allIds, not(hasItems(idOfPostedOrder)));
    }

    @Test
    public void returnsAllOrdersWithOrderRows() {
        deleteAllOrders();

        String randomString = getRandomString(4, 6);

        String orderNumber1 = randomString + "o1";
        String orderNumber2 = randomString + "o2";
        String orderItem1 = randomString + "i1";
        String orderItem2 = randomString + "i2";
        String orderItem3 = randomString + "i3";
        String orderItem4 = randomString + "i4";

        postOrder("api/orders", createOrder(orderNumber1, orderItem1, orderItem2));
        postOrder("api/orders", createOrder(orderNumber2, orderItem3, orderItem4));

        List<Order> orderList = getList("api/orders");

        List<String> returnedOrderNumbers = orderList.stream()
                .map(Order::getOrderNumber)
                .toList();

        assertThat(returnedOrderNumbers,
                containsInAnyOrder(orderNumber1, orderNumber2));

        List<String> orderItemNames = orderList.stream()
                .flatMap(o -> o.getOrderRows().stream())
                .map(OrderRow::getItemName)
                .toList();

        assertThat(orderItemNames,
                containsInAnyOrder(orderItem1, orderItem2, orderItem3, orderItem4));
    }

    @Test
    public void doesNotAllowSqlInjection() {
        String name = "\"';,.:\n";

        Order order = new Order(name);
        order.add(new OrderRow(name, 0, 0));

        Result<Order> result = postOrder("api/orders", order);

        String idOfPostedOrder = result.getValue().getId();

        Order readOrder = getOne("api/orders", param("id", idOfPostedOrder));
        readOrder.setId(null);

        assertThat(readOrder, equalTo(order));
    }

    private void deleteAllOrders() {
        List<Order> orderList = getList("api/orders");

        List<String> orderIds = orderList.stream()
                .map(Order::getId)
                .toList();

        for (String orderId : orderIds) {
            delete("api/orders", param("id", orderId));
        }

        assertThat(getList("api/orders").size(), equalTo(0));
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }

}
