package tests;

import org.junit.Test;
import tests.model.*;
import util.PenaltyOnTestFailure;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class Hw2 extends AbstractHw {

    private final String BASE_URL = "http://localhost:8080";

    @Test
    @PenaltyOnTestFailure(10)
    public void baseUrlResponds() {
        boolean isSuccess = sendRequest(getBaseUrl());

        assertThat(isSuccess, is(true));
    }

    @Test
    @PenaltyOnTestFailure(10)
    public void readsInputAndProducesOutputWithId() {
        String order = "{ \"orderNumber\": \"A123\" }";

        Result<Order> result = postOrderFromJsonString("api/orders", order);

        assertThat(result.isSuccess(), is(true));

        assertThat(result.getValue().getOrderNumber(), is("A123"));
        assertThat(result.getValue().getId(), is(notNullValue()));
    }

    @Test
    @PenaltyOnTestFailure(10)
    public void ignoresWhiteSpace() {
        String order = " {\"orderNumber\":\"A123\"} ";

        Result<Order> result = postOrderFromJsonString("api/orders", order);

        assertThat(result.isSuccess(), is(true));

        assertThat(result.getValue().getOrderNumber(), is("A123"));
    }

    @Test
    @PenaltyOnTestFailure(10)
    public void eachOrderGetsDifferentId() {

        String order1 = "{ \"orderNumber\": \"A123\" }";
        String order2 = "{ \"orderNumber\": \"A456\" }";

        Result<Order> result1 = postOrderFromJsonString(
                "api/orders", order1);
        Result<Order> result2 = postOrderFromJsonString(
                "api/orders", order2);

        assertThat(result1.isSuccess(), is(true));
        assertThat(result2.isSuccess(), is(true));

        assertThat(result1.getValue().getOrderNumber(), is("A123"));
        assertThat(result2.getValue().getOrderNumber(), is("A456"));
        assertThat(result1.getValue().getId(), is(notNullValue()));
        assertThat(result1.getValue().getId(), is(not(result2.getValue().getId())));
    }

    @Test
    @PenaltyOnTestFailure(2)
    public void nullOnIdFieldIsIgnored() {

        String order = "{ \"id\": null, \"orderNumber\": \"A456\" }";

        Result<Order> result = postOrderFromJsonString(
                "api/orders", order);

        assertThat(result.isSuccess(), is(true));

        assertThat(result.getValue().getOrderNumber(), is("A456"));
        assertThat(result.getValue().getId(), is(notNullValue()));
    }

    @Override
    protected String getBaseUrl() {
        return BASE_URL;
    }

}
