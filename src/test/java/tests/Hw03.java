package tests;

import org.junit.Test;
import tests.model.Order;
import tests.model.Result;
import util.IfThisTestFailsMaxPoints;
import util.JsonValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;


public class Hw03 extends AbstractHw {

    private final String baseUrl = "http://localhost:8080";

    @Test
    public void baseUrlResponds() {
        boolean isSuccess = sendRequest(getBaseUrl());

        assertThat(isSuccess, is(true));
    }

    @Test
    public void readsInputAndProducesOutputWithId() {
        String order = "{ \"orderNumber\": \"A123\" }";

        Result<Order> result = postOrderFromJsonString("api/orders", order);

        assertThat(result.isSuccess(), is(true));

        assertThat(result.getValue().getOrderNumber(), is("A123"));
        assertThat(result.getValue().getId(), is(notNullValue()));
    }

    @Test
    public void ignoresWhiteSpace() {
        String order = " {\"orderNumber\":\"A123\"} ";

        Result<Order> result = postOrderFromJsonString("api/orders", order);

        assertThat(result.isSuccess(), is(true));

        assertThat(result.getValue().getOrderNumber(), is("A123"));
    }

    @Test
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
    public void nullOnIdFieldIsIgnored() {

        String order = "{ \"id\": null, \"orderNumber\": \"A456\" }";

        Result<Order> result = postOrderFromJsonString(
                "api/orders", order);

        assertThat(result.isSuccess(), is(true));

        assertThat(result.getValue().getOrderNumber(), is("A456"));
        assertThat(result.getValue().getId(), is(notNullValue()));
    }

    @Test
    @IfThisTestFailsMaxPoints(3)
    public void parserCanHandleArbitraryKeys() {

        Map<String, JsonValue> randomMap = getRandomMap();

        Map<String, Object> inputMap = toObjectMap(randomMap);

        Result<Map<String, Object>> result =
                postMap("api/parser", inputMap);

        Map<String, Object> expected = toExpectedObjectMap(randomMap);

        assertThat(result.isSuccess(), is(true));

        assertThat(result.getValue(), is(expected));
    }

    private Map<String, Object> toObjectMap(
            Map<String, JsonValue> jsonValueMap) {

        return jsonValueMap.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> entry.getValue().getValue()));
    }

    public Map<String, Object> toExpectedObjectMap(
            Map<String, JsonValue> inputMap) {

        Map<String, Object> outputMap = new HashMap<>();

        for (String key : inputMap.keySet()) {

            JsonValue value = inputMap.get(key);

            if (value.isString() && key.length() <= 3) {
                outputMap.put(key, value.getString());
            } else if (value.isString() && key.length() > 3) {
                outputMap.put(key, key);
            } else if (value.isInteger() && key.length() <= 3) {
                outputMap.put(key, value.getInteger());
            } else if (value.isInteger() && key.length() > 3) {
                outputMap.put(key, value.getInteger() * 2);
            } else {
                throw new IllegalArgumentException("unexpected type");
            }
        }

        return outputMap;
    }

    private Map<String, JsonValue> getRandomMap() {
        Map<String, JsonValue> map = new HashMap<>();

        int count = new Random().nextInt(5) + 8;

        for (int i = 0; i < count; i++) {
            map.put(getRandomString(), getRandomValue());
        }

        return map;
    }

    private JsonValue getRandomValue() {
        Random random = new Random();
        return random.nextInt(2) > 0
                ? new JsonValue(getRandomString())
                : new JsonValue(random.nextInt(5));
    }

    private String getRandomString() {
        Random random = new Random();

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < random.nextInt(4) + 2; i++) {
            int code = random.nextInt(23);
            result.append(Character.toChars(code + 'a'));
        }

        return result.toString();
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }

}
