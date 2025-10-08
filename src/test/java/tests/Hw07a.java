package tests;

import org.junit.jupiter.api.Test;
import tests.model.Order;
import tests.model.OrderRow;
import util.FileReader;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Hw07a extends AbstractHw {

    private final String baseUrl = "http://localhost:8080/";

    private final String pathToProjectSourceCode = "";

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

    private void deleteAllOrdersUsingCustomFramework() {
        List<Order> orderList = getList("api/v2/orders");

        List<String> orderIds = orderList.stream()
                .map(Order::getId)
                .toList();

        for (String orderId : orderIds) {
            delete("api/v2/orders/" + orderId);
        }

        assertThat(getList("api/v2/orders")).isEmpty();
    }

    @Test
    public void shouldNotUseSpringMvc() {
        assumeProjectSourcePathIsSet(pathToProjectSourceCode);

        List<FileReader.File> sourceCode = getProjectSource(pathToProjectSourceCode);

        assertDoesNotContainString(sourceCode, "@EnableWebMvc");
        assertDoesNotContainString(sourceCode, "SpringApplication");
        assertDoesNotContainString(sourceCode, "springframework.boot");
        assertDoesNotContainString(sourceCode, "springframework.web");
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }
}
