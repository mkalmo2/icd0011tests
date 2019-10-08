package tests;

import org.junit.Test;
import tests.model.Order;
import tests.model.OrderRow;
import tests.model.Result;
import util.JavaFileReader;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class Hw6 extends AbstractHw {

    private final String baseUrl = "http://localhost:8080/";

    private final String pathToProjectSourceCode = "";

    @Test
    public void baseUrlResponds() {
        boolean isSuccess = sendRequest(getBaseUrl());

        assertThat(isSuccess, is(true));
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
        assertThat(read.getOrderRows().get(0).getItemName(), is("CPU"));
        assertThat(read.getOrderRows().get(1).getItemName(), is("Motherboard"));
    }

    @Test
    public void returnsErrorWhenOrderNumberIsNotValid() {
        Result<Order> result = postOrder("api/orders", new Order("A"));

        assertThat(result.isSuccess(), is(false));
        assertThat(result.getErrors().size(), is(1));
    }

    @Test
    public void shouldUseSpringInsteadOfLowerLeverClasses() {
        assertProjectSourcePathIsSet(pathToProjectSourceCode);

        List<JavaFileReader.File> sourceCode = getProjectSource(pathToProjectSourceCode);

        assertDoesNotContainString(sourceCode, "getConnection");
        assertDoesNotContainString(sourceCode, "createStatement");
        assertDoesNotContainString(sourceCode, "prepareStatement");
        assertDoesNotContainString(sourceCode, "executeUpdate");
        assertDoesNotContainString(sourceCode, "executeQuery");
        assertDoesNotContainString(sourceCode, "getGeneratedKeys");
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }

}
