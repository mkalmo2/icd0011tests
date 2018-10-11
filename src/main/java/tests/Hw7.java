package tests;

import org.junit.Test;
import tests.model.*;
import util.*;

import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class Hw7 extends AbstractHw {

    public static String pathToProjectSourceCode = "";

    private final String BASE_URL = "http://localhost:8080/";

    @Test
    @PenaltyOnTestFailure(10)
    public void getOrderUrlIsChanged() {
        Result<Order> result = postOrder("api/orders", new Order("A123"));

        Long idOfPostedOrder = result.getValue().getId();

        Order order = getOne("api/orders/" + idOfPostedOrder);

        assertThat(order.getOrderNumber(), is("A123"));
        assertThat(order.getId(), is(idOfPostedOrder));
    }

    @Test
    @PenaltyOnTestFailure(4)
    public void returnsErrorsWhenOrderRowsAreNotValid() {
        Order order = new Order("A1");
        order.add(new OrderRow("Motherboard", 0, 5));
        order.add(new OrderRow("Cpu", 5, 0));

        Result<Order> result = postOrder("api/orders", order);

        assertThat(result.isSuccess(), is(false));
        assertThat(result.getErrors().size(), is(2));
    }

    @Test
    @PenaltyOnTestFailure(10)
    public void shouldUseSpringMvcInsteadOfLowerLeverClasses() {
        String sourceCode = new JavaFileReader()
                .getAllFilesFrom(Paths.get(pathToProjectSourceCode));

        assertThat(sourceCode, not(containsString("getConnection")));
        assertThat(sourceCode, not(containsString("getGeneratedKeys")));
        assertThat(sourceCode, not(containsString("HttpServlet")));
        assertThat(sourceCode, not(containsString("@WebListener")));
        assertThat(sourceCode, not(containsString("@WebServlet")));
    }

    @Override
    protected String getBaseUrl() {
        return BASE_URL;
    }

}
