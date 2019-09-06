package tests;

import org.junit.Test;
import tests.model.Order;
import tests.model.Result;
import util.PenaltyOnTestFailure;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
    @PenaltyOnTestFailure(10)
    public void nullOnIdFieldIsIgnored() {

        String order = "{ \"id\": null, \"orderNumber\": \"A456\" }";

        Result<Order> result = postOrderFromJsonString(
                "api/orders", order);

        assertThat(result.isSuccess(), is(true));

        assertThat(result.getValue().getOrderNumber(), is("A456"));
        assertThat(result.getValue().getId(), is(notNullValue()));
    }

    @Test
    @PenaltyOnTestFailure(4)
    public void parserCanHandleArbitraryKeys() {

        Map<String, String> inputMap = getRandomMap();

        Result<Map<String, String>> result = postMap("api/parser", inputMap);

        assertThat(result.isSuccess(), is(true));

        Map<String, String> outputMap = result.getValue();

        assertThat(inputMap.size(), is(outputMap.size()));

        assertCorrectlyTransformed(inputMap, outputMap);
    }

    private void assertCorrectlyTransformed(Map<String, String> inputMap,
                                            Map<String, String> outputMap) {

        for (String key : inputMap.keySet()) {
            String reversed = new StringBuilder(key).reverse().toString();
            String keyLengthAsString = String.valueOf(key.length());

            assertThat(outputMap.get(reversed), is(keyLengthAsString));
        }
    }

    private Map<String, String> getRandomMap() {
        Map<String, String> map = new HashMap<>();

        int count = new Random().nextInt(5) + 5;

        for (int i = 0; i < count; i++) {
            map.put(getRandomString(), String.valueOf(0));
        }

        return map;
    }

    private String getRandomString() {
        Random random = new Random();

        StringBuffer result = new StringBuffer();
        for (int i = 0; i < random.nextInt(6) + 2; i++) {
            int code = random.nextInt(23);
            result.append(Character.toChars(code + 65));
        }

        return result.toString();
    }

    @Override
    protected String getBaseUrl() {
        return BASE_URL;
    }

}
