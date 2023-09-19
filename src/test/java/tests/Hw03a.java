package tests;

import org.junit.Test;
import tests.model.Result;
import util.JsonValue;
import util.SampleDataProvider;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class Hw03a extends AbstractHw {

    private final String baseUrl = "http://localhost:8080";

    @Test
    public void parserCanHandleMoreComplexJson() {
        JsonValue data = new SampleDataProvider(3).getRandomData();

        Map<String, Object> inputMap = toObjectMap(data);

        Result<List<Object>> result =
                postMap("api/parser", inputMap);

        List<Object> expected = toExpectedList(data);

        assertThat(result.isSuccess(), is(true));

        assertThat(result.getValue(), is(expected));
    }

    private Map<String, Object> toObjectMap(JsonValue data) {
        return data.getMap().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getValue()));
    }

    public List<Object> toExpectedList(JsonValue node) {

        modifyIntegerValues(node, 0);

        Queue<JsonValue> queue = new LinkedList<>();

        var result = new ArrayList<>();

        queue.add(node);

        while (!queue.isEmpty()) {
            JsonValue current = queue.remove();

            if (current.isMap()) {
                queue.addAll(current.getMap().values());
            } else {
                result.add(current.getValue());
            }
        }

        return result;
    }

    private void modifyIntegerValues(JsonValue node, int currentDepth) {
        if (node.isInteger()) {
            node.setInteger(node.getInteger() * currentDepth);
        } else if (node.isMap()) {
            for (String key : node.getMap().keySet()) {
                modifyIntegerValues(node.getMap().get(key), currentDepth + 1);
            }
        }
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }

}
