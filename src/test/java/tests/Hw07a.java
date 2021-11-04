package tests;

import org.junit.Test;
import tests.model.Order;
import tests.model.OrderRow;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class Hw07a extends AbstractHw {

    private final String baseUrl = "http://localhost:8080/";

    @Test
    public void returnsAllOrdersWithOrderRowsUsingCustomFramework() {
        deleteAllOrdersUsingCustomFramework();

        String randomString = getRandomString(4, 6);

        String orderNumber1 = randomString + "o1";
        String orderNumber2 = randomString + "o2";
        String orderItem1 = randomString + "i1";
        String orderItem2 = randomString + "i2";
        String orderItem3 = randomString + "i3";
        String orderItem4 = randomString + "i4";

        postOrder("api/v2/orders", createOrder(orderNumber1, orderItem1, orderItem2));
        postOrder("api/v2/orders", createOrder(orderNumber2, orderItem3, orderItem4));

        List<Order> orderList = getList("api/v2/orders");

        List<String> returnedOrderNumbers = orderList.stream()
                .map(Order::getOrderNumber)
                .collect(Collectors.toList());

        assertThat(returnedOrderNumbers,
                containsInAnyOrder(orderNumber1, orderNumber2));

        List<String> orderItemNames = orderList.stream()
                .flatMap(o -> o.getOrderRows().stream())
                .map(OrderRow::getItemName)
                .collect(Collectors.toList());

        assertThat(orderItemNames,
                containsInAnyOrder(orderItem1, orderItem2, orderItem3, orderItem4));
    }

    private void deleteAllOrdersUsingCustomFramework() {
        List<Order> orderList = getList("api/v2/orders");

        List<String> orderIds = orderList.stream()
                .map(Order::getId)
                .collect(Collectors.toList());

        for (String orderId : orderIds) {
            delete("api/v2/orders/" + orderId);
        }

        assertThat(getList("api/v2/orders").size(), equalTo(0));
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }
}
