package tests;

import org.junit.Test;
import tests.model.Order;
import tests.model.OrderRow;
import util.JavaFileReader;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class Hw09 extends AbstractHw {

    private final String baseUrl = "http://localhost:8080/";

    private final String pathToProjectSourceCode = "";

    @Test
    public void addsOrderWithOrderRows() {

        String id1 = postOrder("api/orders", "A123", "CPU", "Motherboard");
        String id2 = postOrder("api/orders", "A456", "Mouse");
        String id3 = postOrder("api/orders", "A789", "Monitor", "Printer");

        List<Order> orderList = getList("api/orders");

        assertHasIds(orderList, id1, id2, id3);

        assertContainsItems(orderList, id1, "CPU", "Motherboard");
        assertContainsItems(orderList, id2, "Mouse");
        assertContainsItems(orderList, id3, "Monitor", "Printer");
    }

    @Test
    public void shouldUseOnlyJpaForDatabaseAccess() {
        assertProjectSourcePathIsSet(pathToProjectSourceCode);

        List<JavaFileReader.File> sourceCode = getProjectSource(pathToProjectSourceCode);

        assertDoesNotContainString(sourceCode, "getConnection");
        assertDoesNotContainString(sourceCode, "createStatement");
        assertDoesNotContainString(sourceCode, "prepareStatement");
        assertDoesNotContainString(sourceCode, "executeQuery");
        assertDoesNotContainString(sourceCode, "getGeneratedKeys");

        assertDoesNotContainString(sourceCode, "JdbcTemplate");
        assertDoesNotContainString(sourceCode, "SimpleJdbcInsert");
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }

    private Order getOrder(String number, String ... items) {
        Order order = new Order(number);
        for (String item : items) {
            order.add(new OrderRow(item, 1, 100));
        }
        return order;
    }

    private String postOrder(String url, String number, String ... items) {
        return postOrder(url, getOrder(number, items))
                .getValue()
                .getId();
    }

    private void assertContainsItems(
            List<Order> orderList, String id, String ... itemName) {

        List<String> items = orderList.stream()
                .filter(o -> o.getId().equals(id))
                .flatMap(o -> o.getOrderRows().stream())
                .map(OrderRow::getItemName)
                .collect(Collectors.toList());

        assertThat(items, contains(itemName));
    }

    private void assertHasIds(List<Order> orderList, String ... ids) {
        List<String> returnedOrderIds = orderList.stream()
                .map(order -> order.getId())
                .collect(Collectors.toList());

        assertThat(returnedOrderIds, hasItems(ids));
    }


}
