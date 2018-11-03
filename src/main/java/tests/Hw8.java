package tests;

import org.junit.Test;
import tests.model.Order;
import tests.model.OrderRow;
import util.JavaFileReader;
import util.PenaltyOnTestFailure;

import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class Hw8 extends AbstractHw {

    public static String pathToProjectSourceCode = "";

    private final String BASE_URL = "http://localhost:8080/";

    @Test
    @PenaltyOnTestFailure(10)
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
    @PenaltyOnTestFailure(10)
    public void shouldNotUseSpringBoot() {
        String sourceCode = new JavaFileReader()
                .getAllFilesFrom(Paths.get(pathToProjectSourceCode))
                .replaceAll("\\s", "");

        assertThat(sourceCode, not(containsString("SpringApplication")));
        assertThat(sourceCode, not(containsString("springframework.boot")));
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
        return BASE_URL;
    }

}
