package tests;

import org.junit.Test;
import tests.model.Order;
import tests.model.OrderRow;
import util.JavaFileReader;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class Hw8 extends AbstractHw {

    private final String baseUrl = "http://localhost:8080/";

    private final String pathToProjectSourceCode = "";

    @Test
    public void addsOrderWithOrderRows() {
        delete("api/orders");

        postOrder("api/orders", getOrder("A123", "CPU", "Motherboard"));
        postOrder("api/orders", getOrder("A123", "Mouse"));
        postOrder("api/orders", getOrder("A123", "Monitor", "Printer"));

        List<Order> orders = getList("api/orders");

        assertThat(orders.size(), is(3));
        assertThat(orders.get(2).getOrderRows().size(), is(2));
        assertThat(orders.get(2).getOrderRows().get(1).getItemName(), is("Printer"));
    }

    @Test
    public void shouldNotUseSpringBoot() {
        assertProjectSourcePathIsSet(pathToProjectSourceCode);

        List<JavaFileReader.File> sourceCode = getProjectSource(pathToProjectSourceCode);

        assertDoesNotContainString(sourceCode, "SpringApplication");
        assertDoesNotContainString(sourceCode, "springframework.boot");
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

    private Order getOrder(String number, String ... items) {
        Order order = new Order(number);
        for (String item : items) {
            order.add(new OrderRow(item, 1, 100));
        }
        return order;
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }

}
