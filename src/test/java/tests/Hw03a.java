package tests;

import org.junit.Test;
import tests.model.Result;
import util.JsonValue;
import util.SampleDataProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class Hw03a extends AbstractHw {

    private final String baseUrl = "http://localhost:8080";

    @Test
    public void parserCanHandleArbitraryKeys() {

        JsonValue data = new SampleDataProvider(5).getRandomData();

        Map<String, Object> inputMap = toObjectMap(data);

        Result<Map<String, Object>> result =
                postMap("api/parser", inputMap);

        Map<String, Object> expected = toExpectedObjectMap(data);

        assertThat(result.isSuccess(), is(true));

        assertThat(result.getValue(), is(expected));
    }

    private Map<String, Object> toObjectMap(JsonValue data) {

        return data.getMap().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getValue()));
    }

    public Map<String, Object> toExpectedObjectMap(JsonValue data) {

        Map<String, Object> outputMap = new HashMap<>();

        for (String key : data.getMap().keySet()) {

            JsonValue value = data.getMap().get(key);

            if (value.isString() && key.length() <= 3) {
                outputMap.put(key, value.getString());
            } else if (value.isString()) {
                outputMap.put(key, key);
            } else if (value.isInteger()) {
                outputMap.put(key, value.getInteger() * 2);
            } else if (value.isMap()) {
                outputMap.put(key, toExpectedObjectMap(value));
            } else {
                throw new IllegalArgumentException("unexpected type");
            }
        }

        return outputMap;
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }

}
