package tests;

import jakarta.ws.rs.client.Invocation;
import org.junit.jupiter.api.Test;
import tests.model.Order;
import tests.model.OrderRow;
import tests.model.Result;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class Hw04 extends AbstractHw {

    private final String baseUrl = "http://localhost:8080";

    @Test
    public void canGetOrderById() {
        Result<Order> result = postOrder("api/orders", new Order("A123"));

        assertThat(result.isSuccess()).isTrue();

        String idOfPostedOrder = result.getValue().getId();

        Order order = getOne("api/orders", param("id", idOfPostedOrder));

        assertThat(order.getOrderNumber()).isEqualTo("A123");
        assertThat(order.getId()).isEqualTo(idOfPostedOrder);
    }

    @Test
    public void addsOrderWithOrderRows() {
        Order order = new Order("A456");
        order.add(new OrderRow("CPU", 2, 100));
        order.add(new OrderRow("Motherboard", 3, 60));

        Result<Order> result = postOrder("api/orders", order);

        assertThat(result.isSuccess()).isTrue();

        String idOfPostedOrder = result.getValue().getId();

        Order read = getOne("api/orders", param("id", idOfPostedOrder));

        assertThat(read.getOrderRows()).hasSize(2);
        assertThat(read.getOrderRows().get(1).getItemName())
                .isEqualTo("Motherboard");
    }

    @Test
    public void addsOrderFromFormInput() {
        String expectedReturnType = MediaType.APPLICATION_FORM_URLENCODED;

        Order order = postForm("orders/form",
                getForm("orderNumber", "A789"), expectedReturnType);

        Order read = getOne("api/orders", param("id", order.getId()));

        assertThat(read.getOrderNumber()).isEqualTo("A789");
    }

    @Test
    public void shouldReturnExpectedContentType() {
        Order order = postForm("orders/form",
                getForm("orderNumber", "A789"), MediaType.APPLICATION_JSON);

        Order read = getOne("api/orders", param("id", order.getId()));

        assertThat(read.getOrderNumber()).isEqualTo("A789");
    }

    private Form getForm(String name, String value) {
        Form form = new Form();
        form.param(name, value);
        return form;
    }

    private Order postForm(String path, Form form, String acceptedResponseType) {
        Invocation.Builder request = getTarget()
                .path(path)
                .request(acceptedResponseType);

        var requestType = MediaType.APPLICATION_FORM_URLENCODED_TYPE;

        try (Response response = request.post(Entity.entity(form, requestType))) {

            return MediaType.APPLICATION_FORM_URLENCODED.equals(acceptedResponseType)
                    ? fromWwwUrlencoded(response.readEntity(String.class))
                    : response.readEntity(Order.class);
        }
    }

    private static Order fromWwwUrlencoded(String urlencodedFormData) {
        Map<String, String> map = new LinkedHashMap<>();
        for (String pair: urlencodedFormData.split("&")) {
            String[] keyValue = pair.split("=");
            map.put(keyValue[0], URLDecoder.decode(keyValue[1],
                    StandardCharsets.UTF_8));
        }

        Order order = new Order();

        order.setId(map.get("id"));
        order.setOrderNumber(map.get("orderNumber"));

        return order;
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }

}