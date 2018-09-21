package tests;

import org.junit.Test;
import tests.model.Order;
import tests.model.OrderRow;
import tests.model.Result;
import util.PenaltyOnTestFailure;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class Hw5 extends AbstractHw {

    private final String BASE_URL = "http://localhost:8080/";

    @Test
    @PenaltyOnTestFailure(10)
    public void baseUrlResponds() {
        boolean isSuccess = sendRequest(getBaseUrl());

        assertThat(isSuccess, is(true));
    }

    @Test
    @PenaltyOnTestFailure(10)
    public void returnsPostedOrderWithAddedId() {
        Order order = new Order("A123");

        Result<Order> result = postOrder("api/orders", order);

        Long idOfPostedOrder = result.getValue().getId();

        assertThat(idOfPostedOrder, is(notNullValue()));
    }

    @Test
    @PenaltyOnTestFailure(10)
    public void addsOrderWithOrderRows() {
        Order order = new Order("A456");
        order.add(new OrderRow("CPU", 2, 100));
        order.add(new OrderRow("Motherboard", 3, 60));

        Result<Order> result = postOrder("api/orders", order);

        Long idOfPostedOrder = result.getValue().getId();

        Order read = getOne("api/orders", param("id", idOfPostedOrder));

        assertThat(read.getOrderRows().size(), is(2));
        assertThat(read.getOrderRows().get(1).getItemName(), is("Motherboard"));
    }

    @Test
    @PenaltyOnTestFailure(2)
    public void deletesOrderById() {

        Result<Order> result = postOrder("api/orders", new Order("A1"));

        Long idOfPostedOrder = result.getValue().getId();

        delete("api/orders", param("id", idOfPostedOrder));

        List<Order> allOrders = getList("api/orders");

        List<Long> allIds = allOrders.stream()
                .map(o -> o.getId())
                .collect(Collectors.toList());

        assertThat(allIds, not(hasItems(idOfPostedOrder)));
    }

    @Override
    protected String getBaseUrl() {
        return BASE_URL;
    }

}
