package tests;

import org.junit.jupiter.api.Test;
import tests.model.Order;
import tests.model.OrderRow;
import tests.model.Result;
import util.FileReader;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Hw07 extends AbstractHw {

    private final String baseUrl = "http://localhost:8080/";

    private final String pathToProjectSourceCode = "";

    @Test
    public void baseUrlResponds() {
        boolean isSuccess = sendRequest(getBaseUrl());

        assertThat(isSuccess).isTrue();
    }

    @Test
    public void findsOrderById() {
        String randomString = getRandomString(4, 6);

        String orderNumber = randomString + "o1";
        String orderItem1 = randomString + "i1";
        String orderItem2 = randomString + "i2";

        Result<Order> result = postOrder("api/orders",
                createOrder(orderNumber, orderItem1, orderItem2));

        String idOfPostedOrder = result.getValue().getId();

        Order read = getOne("api/orders", param("id", idOfPostedOrder));

        assertThat(read.getOrderNumber()).isEqualTo(orderNumber);
        assertThat(read.getOrderRows()).hasSize(2);
        assertThat(read.getOrderRows().get(0).getItemName()).isEqualTo(orderItem1);
        assertThat(read.getOrderRows().get(1).getItemName()).isEqualTo(orderItem2);
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

        assertThat(allIds).doesNotContain(idOfPostedOrder);
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

        assertThat(returnedOrderNumbers)
                .containsExactlyInAnyOrder(orderNumber1, orderNumber2);

        List<String> orderItemNames = orderList.stream()
                .flatMap(o -> o.getOrderRows().stream())
                .map(OrderRow::getItemName)
                .toList();

        assertThat(orderItemNames)
                .containsExactlyInAnyOrder(orderItem1, orderItem2, orderItem3, orderItem4);
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

        assertThat(readOrder).isEqualTo(order);
    }

    @Test
    public void returnsErrorWhenOrderNumberIsNotValid() {
        Result<Order> result = postOrder("api/orders", new Order("A"));

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrors()).hasSize(1);
    }

    @Test
    public void shouldUseSpringInsteadOfLowerLeverClasses() {
        assumeProjectSourcePathIsSet(pathToProjectSourceCode);

        List<FileReader.File> sourceCode = getProjectSource(pathToProjectSourceCode);

        assertDoesNotContainString(sourceCode, "getConnection");
        assertDoesNotContainString(sourceCode, "createStatement");
        assertDoesNotContainString(sourceCode, "prepareStatement");
        assertDoesNotContainString(sourceCode, "executeUpdate");
        assertDoesNotContainString(sourceCode, "executeQuery");
        assertDoesNotContainString(sourceCode, "getGeneratedKeys");
    }

    private void deleteAllOrders() {
        List<Order> orderList = getList("api/orders");

        List<String> orderIds = orderList.stream()
                .map(Order::getId)
                .toList();

        for (String orderId : orderIds) {
            delete("api/orders", param("id", orderId));
        }

        assertThat(getList("api/orders")).isEmpty();
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }
}
