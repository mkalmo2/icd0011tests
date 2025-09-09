package tests;

import org.junit.jupiter.api.Test;
import tests.model.Order;
import tests.model.Result;

import static org.assertj.core.api.Assertions.assertThat;


public class Hw03 extends AbstractHw {

    private final String baseUrl = "http://localhost:8080";

    @Test
    public void baseUrlResponds() {
        boolean isSuccess = sendRequest(getBaseUrl());

        assertThat(isSuccess).isTrue();
    }

    @Test
    public void readsInputAndProducesOutputWithId() {
        String order = "{ \"orderNumber\": \"A123\" }";

        Result<Order> result = postOrderFromJsonString("api/orders", order);

        assertThat(result.isSuccess()).isTrue();

        assertThat(result.getValue().getOrderNumber()).isEqualTo("A123");
        assertThat(result.getValue().getId()).isNotNull();
    }

    @Test
    public void ignoresWhiteSpace() {
        String order = " {\"orderNumber\":\"A123\"} ";

        Result<Order> result = postOrderFromJsonString("api/orders", order);

        assertThat(result.isSuccess()).isTrue();

        assertThat(result.getValue().getOrderNumber()).isEqualTo("A123");
    }

    @Test
    public void eachOrderGetsDifferentId() {

        String order1 = "{ \"orderNumber\": \"A123\" }";
        String order2 = "{ \"orderNumber\": \"A456\" }";

        Result<Order> result1 = postOrderFromJsonString(
                "api/orders", order1);
        Result<Order> result2 = postOrderFromJsonString(
                "api/orders", order2);

        assertThat(result1.isSuccess()).isTrue();
        assertThat(result2.isSuccess()).isTrue();

        assertThat(result1.getValue().getOrderNumber()).isEqualTo("A123");
        assertThat(result2.getValue().getOrderNumber()).isEqualTo("A456");
        assertThat(result1.getValue().getId()).isNotNull();
        assertThat(result1.getValue().getId()).isNotEqualTo(result2.getValue().getId());
    }

    @Test
    public void nullOnIdFieldIsIgnored() {

        String order = "{ \"id\": null, \"orderNumber\": \"A456\" }";

        Result<Order> result = postOrderFromJsonString(
                "api/orders", order);

        assertThat(result.isSuccess()).isTrue();

        assertThat(result.getValue().getOrderNumber()).isEqualTo("A456");
        assertThat(result.getValue().getId()).isNotNull();
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }

}