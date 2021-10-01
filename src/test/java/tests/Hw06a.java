package tests;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.Test;
import tests.model.Order;
import tests.model.OrderRow;
import tests.model.Result;
import tests.model.ValidationErrors;
import util.SampleDataProvider;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class Hw06a extends AbstractHw {

    private final String baseUrl = "http://localhost:8080/";

    @Test
    public void bulkInsertOrders() {

        List<Order> orderList = generateOrderList();

        List<String> ids = postOrderList("api/orders/bulk", orderList).getValue();

        assertThat(ids.size(), is(orderList.size()));

        Map<String, Order> orderMap = pairWithIds(orderList, ids);

        List<String> idsToTest =  getRandomIdsFrom(ids);

        for (String id : idsToTest) {
            Order read = getOne("api/orders", param("id", id));

            assertThat(read, is(orderMap.get(id)));
        }
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
            String randomString = getRandomString() + ",\";";

            String orderNumber1 = randomString + "o1";
            String orderItem1 = randomString + "i1";
            String orderItem2 = randomString + "i2";

            orderList.add(createOrder(orderNumber1, orderItem1, orderItem2));
        }

        return orderList;
    }

    protected Result<List<String>> postOrderList(String path, List<Order> orders) {
        Response response = getTarget()
                .path(path)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(orders, MediaType.APPLICATION_JSON));

        Result<List<String>> result = new Result<>();

        if (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            result.setErrors(response.readEntity(ValidationErrors.class).getErrors());
        } else {
            result.setValue(response.readEntity(new GenericType<List<String>>() {}));
        }

        return result;
    }

    private String getRandomString() {
        return new SampleDataProvider(0).getRandomString(4, 6);
    }

    private Order createOrder(String number, String ... items) {
        Order order = new Order(number);
        for (String item : items) {
            order.add(new OrderRow(item, 1, 1));
        }
        return order;
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }

}
