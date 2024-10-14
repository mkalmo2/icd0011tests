package tests;

import org.junit.Test;
import tests.model.Order;
import tests.model.OrderRow;
import tests.model.Result;
import util.FileReader;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class Hw08 extends AbstractHw {

    private final String baseUrl = "http://localhost:8080/";

    private final String pathToProjectSourceCode = "";

    @Test
    public void getOrderUrlIsChanged() {
        Result<Order> result = postOrder("api/orders", new Order("A123"));

        String idOfPostedOrder = result.getValue().getId();

        Order order = getOne("api/orders/" + idOfPostedOrder);

        assertThat(order.getOrderNumber(), is("A123"));
        assertThat(order.getId(), is(idOfPostedOrder));
    }

    @Test
    public void returnsErrorsWhenOrderRowsAreNotValid() {
        Order order = new Order("A1");
        order.add(new OrderRow("Motherboard", 0, 5));
        order.add(new OrderRow("Cpu", 5, 0));

        Result<Order> result = postOrder("api/orders", order);

        assertThat(result.isSuccess(), is(false));
        assertThat(result.getErrors().size(), is(2));
    }

    @Test
    public void shouldUseSpringMvcInsteadOfLowerLevelClasses() {
        assumeProjectSourcePathIsSet(pathToProjectSourceCode);

        List<FileReader.File> sourceCode = getProjectSource(pathToProjectSourceCode);

        assertDoesNotContainString(sourceCode, "HttpServlet");
        assertDoesNotContainString(sourceCode, "@WebListener");
        assertDoesNotContainString(sourceCode, "@WebServlet");
    }

    @Test
    public void shouldNotUseSpringBootYet() {
        assumeProjectSourcePathIsSet(pathToProjectSourceCode);

        List<FileReader.File> sourceCode = getProjectSource(pathToProjectSourceCode);

        assertDoesNotContainString(sourceCode, "SpringApplication");
        assertDoesNotContainString(sourceCode, "springframework.boot");
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }
}
