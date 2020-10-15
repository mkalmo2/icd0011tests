package tests;

import org.junit.Test;
import tests.model.Installment;
import tests.model.Order;
import tests.model.OrderRow;
import tests.model.Result;
import util.IfThisTestFailsMaxPoints;
import util.JavaFileReader;
import util.Parameter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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
    public void shouldUseSpringMvcInsteadOfLowerLeverClasses() {
        assertProjectSourcePathIsSet(pathToProjectSourceCode);

        List<JavaFileReader.File> sourceCode = getProjectSource(pathToProjectSourceCode);

        assertDoesNotContainString(sourceCode, "HttpServlet");
        assertDoesNotContainString(sourceCode, "@WebListener");
        assertDoesNotContainString(sourceCode, "@WebServlet");
    }

    @Test
    public void shouldNotUseSpringBoot() {
        assertProjectSourcePathIsSet(pathToProjectSourceCode);

        List<JavaFileReader.File> sourceCode = getProjectSource(pathToProjectSourceCode);

        assertDoesNotContainString(sourceCode, "SpringApplication");
        assertDoesNotContainString(sourceCode, "springframework.boot");
    }

    @Test
    @IfThisTestFailsMaxPoints(3)
    public void amountDividesEquallyBetweenInstallments() {
        String actual = stringify(generateInstallmentsFor(
                9, "2020-11-04", "2021-01-01"));

        String expected = "[3, 2020-11-04], [3, 2020-12-01], [3, 2021-01-01]";

        assertThat(actual, is(expected));
    }

    @Test
    @IfThisTestFailsMaxPoints(3)
    public void installmentPeriodTooLongForTheAmount() {
        String actual = stringify(generateInstallmentsFor(
                6, "2020-11-04", "2021-01-01"));

        String expected = "[3, 2020-11-04], [3, 2020-12-01]";

        assertThat(actual, is(expected));
    }

    @Test
    @IfThisTestFailsMaxPoints(3)
    public void remainderDividedBetweenLastPayments() {
        String actual = stringify(generateInstallmentsFor(
                11, "2020-11-04", "2021-01-01"));

        String expected = "[3, 2020-11-04], [4, 2020-12-01], [4, 2021-01-01]";

        assertThat(actual, is(expected));
    }

    private List<Installment> generateInstallmentsFor(
            Integer amount, String startDate, String endDate) {

        Order order = new Order("A1");
        order.add(new OrderRow("Motherboard", 1, amount));

        Result<Order> result = postOrder("api/orders", order);

        String idOfPostedOrder = result.getValue().getId();

        String url = String.format("api/orders/%s/installments", idOfPostedOrder);
        Parameter start = new Parameter("start", startDate);
        Parameter end = new Parameter("end", endDate);

        return getInstallmentList(url, start, end);
    }

    private String stringify(List<Installment> installments) {
        return installments.stream()
                .map(this::formatInstallment)
                .collect(Collectors.joining(", "));
    }

    private String formatInstallment(Installment installment) {
        return String.format("[%s, %s]",
                installment.getAmount(),
                installment.getDate().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }
}
