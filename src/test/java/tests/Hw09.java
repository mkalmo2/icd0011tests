package tests;

import org.junit.jupiter.api.Test;
import tests.model.Order;
import util.FileReader;

import java.util.List;

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
        assumeProjectSourcePathIsSet(pathToProjectSourceCode);

        List<FileReader.File> sourceCode = getProjectSource(pathToProjectSourceCode);

        assertDoesNotContainString(sourceCode, "getConnection");
        assertDoesNotContainString(sourceCode, "createStatement");
        assertDoesNotContainString(sourceCode, "prepareStatement");
        assertDoesNotContainString(sourceCode, "executeQuery");
        assertDoesNotContainString(sourceCode, "getGeneratedKeys");

        assertDoesNotContainString(sourceCode, "JdbcTemplate");
        assertDoesNotContainString(sourceCode, "JdbcClient");
        assertDoesNotContainString(sourceCode, "SimpleJdbcInsert");
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }
}
