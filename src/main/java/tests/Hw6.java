package tests;

import org.junit.Test;
import tests.model.Order;
import tests.model.OrderRow;
import tests.model.Report;
import tests.model.Result;
import util.PenaltyOnTestFailure;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class Hw6 extends AbstractHw {

    private final String BASE_URL = "http://localhost:8080/";

    @Test
    @PenaltyOnTestFailure(10)
    public void baseUrlResponds() {
        boolean isSuccess = sendRequest(getBaseUrl());

        assertThat(isSuccess, is(true));
    }

    @Test
    @PenaltyOnTestFailure(10)
    public void canDeleteAllOrders() {
        postOrder("api/orders", new Order("A123"));

        delete("api/orders");

        List<Order> allOrders = getList("api/orders");

        assertThat(allOrders.size(), is(0));
    }

    @Test
    @PenaltyOnTestFailure(10)
    public void generatesReportFromInsertedOrders() {

        delete("api/orders");

        Order order1 = new Order("A1");
        order1.add(new OrderRow("Motherboard", 10, 6));

        Order order2 = new Order("A2");
        order2.add(new OrderRow("CPU", 4, 10));

        postOrder("api/orders", order1);
        postOrder("api/orders", order2);

        Report report = getOne("api/orders/report", Report.class);

        assertThat(report.getCount(), is(2));
        assertThat(report.getAverageOrderAmount(), is(50));
        assertThat(report.getTurnoverWithoutVAT(), is(100));
        assertThat(report.getTurnoverVAT(), is(20));
        assertThat(report.getTurnoverWithVAT(), is(120));
    }

    @Test
    @PenaltyOnTestFailure(2)
    public void returnsErrorsWhenOrderNumberIsNotValid() {
        Result<Order> result = postOrder("api/orders", new Order("A"));

        assertThat(result.isSuccess(), is(false));
        assertThat(result.getErrors().size(), is(1));
    }

    @Override
    protected String getBaseUrl() {
        return BASE_URL;
    }

}
