package tests;

import org.junit.Test;
import tests.model.Installment;
import tests.model.Order;
import tests.model.OrderRow;
import tests.model.Result;
import util.IfThisTestFailsMaxPoints;
import util.JavaFileReader;
import util.Parameter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class Hw7 extends AbstractHw {

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
    public void readMultipleOrders() {
        int num = Timestamp.valueOf(LocalDateTime.now()).getNanos();
        String orderNumber1 = String.valueOf(num);
        String orderNumber2 = String.valueOf(num + 1);

        postOrder("api/orders", getOrder(orderNumber1, "CPU", "Mouse"));
        postOrder("api/orders", getOrder(orderNumber2, "A124", "Motherboard"));

        List<Order> orderList = getList("api/orders");

        List<String> returnedOrderNumbers = orderList.stream()
                .map(order -> order.getOrderNumber())
                .collect(Collectors.toList());

        assertThat(returnedOrderNumbers,
                hasItems(orderNumber1, orderNumber2));
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
    @IfThisTestFailsMaxPoints(6)
    public void amountDividesEquallyBetweenInstallments() {
        String actual = stringify(generateInstallmentsFor(
                9, "2019-11-04", "2020-01-01"));

        String expected = "[3, 2019-11-04], [3, 2019-12-01], [3, 2020-01-01]";

        assertThat(actual, is(expected));
    }

    @Test
    @IfThisTestFailsMaxPoints(6)
    public void installmentPeriodTooLongForTheAmount() {
        String actual = stringify(generateInstallmentsFor(
                6, "2019-11-04", "2020-01-01"));

        String expected = "[3, 2019-11-04], [3, 2019-12-01]";

        assertThat(actual, is(expected));
    }

    @Test
    @IfThisTestFailsMaxPoints(6)
    public void remainderDividedBetweenLastPayments() {
        String actual = stringify(generateInstallmentsFor(
                11, "2019-11-04", "2020-03-01"));

        String expected = "[3, 2019-11-04], [4, 2019-12-01], [4, 2020-01-01]";

        assertThat(actual, is(expected));
    }

    private Order getOrder(String number, String ... items) {
        Order order = new Order(number);
        for (String item : items) {
            order.add(new OrderRow(item, 1, 100));
        }
        return order;
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
