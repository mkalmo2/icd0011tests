package tests;

import jakarta.ws.rs.core.GenericType;
import org.junit.jupiter.api.Test;
import tests.model.Order;
import tests.model.Result;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


public class Hw06a extends AbstractHw {

    private final String baseUrl = "http://localhost:8080/";

    @Test
    public void bulkInsertOrders() {

        List<Order> orderList = generateOrderList();

        List<Order> returnedOrders = postOrderList(
                "api/orders/bulk", orderList).getValue();

        assertThat(returnedOrders).hasSameSizeAs(orderList);

        List<String> ids = extractIds(returnedOrders);

        Map<String, Order> orderMap = pairWithIds(orderList, ids);

        List<String> idsToTest =  getRandomIdsFrom(ids);

        for (String id : idsToTest) {
            Order read = getOne("api/orders", param("id", id));

            assertThat(read).isEqualTo(orderMap.get(id));
        }
    }

    private List<String> extractIds(List<Order> returnedOrders) {
        return returnedOrders.stream().map(Order::getId).toList();
    }

    private List<String> getRandomIdsFrom(List<String> ids) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(ids.get(new Random().nextInt(ids.size())));
        }

        return list;
    }

    private Map<String, Order> pairWithIds(List<Order> orderList, List<String> ids) {
        Map<String, Order> map = new HashMap<>();
        int index = 0;
        for (Order order : orderList) {
            String id = ids.get(index++);
            order.setId(id);
            map.put(id, order);
        }
        return map;
    }

    private List<Order> generateOrderList() {
        List<Order> orderList = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            String randomString = getRandomString(4, 6) + ",\";";

            String orderNumber1 = randomString + "o1";
            String orderItem1 = randomString + "i1";
            String orderItem2 = randomString + "i2";

            orderList.add(createOrder(orderNumber1, orderItem1, orderItem2));
        }

        return orderList;
    }

    protected Result<List<Order>> postOrderList(String path, List<Order> orders) {
        return postCommon(path, orders, new GenericType<>() {});
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }

}
