package util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SampleJsonProvider {

    private final Random rnd = new Random();
    private final int maxDepth;

    public SampleJsonProvider(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public JsonValue getRandomData() {
        var currentDepth = 1;

        return getRandomMap(currentDepth);
    }

    private JsonValue getRandomMap(int currentDepth) {
        Map<String, JsonValue> map = new HashMap<>();

        int keyCount = 7 - currentDepth;
        int mapValuePos = rnd.nextInt(keyCount);

        for (int i = 0; i < keyCount; i++) {
            String key = getRandomString(2, 5);

            JsonValue value = (i == mapValuePos && currentDepth <= maxDepth)
                    ? getRandomMap(currentDepth + 1)
                    : getRandomValue();

            map.put(key, value);
        }

        return new JsonValue(map);
    }

    private JsonValue getRandomValue() {
        return rnd.nextBoolean()
                ? new JsonValue(getRandomString(2, 5))
                : new JsonValue(rnd.nextInt(50));
    }

    private String getRandomString(int minLength, int maxLength) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < minLength + rnd.nextInt(maxLength - minLength + 1); i++) {
            int code = rnd.nextInt(23);
            result.append(Character.toChars(code + 'a'));
        }

        return result.toString();
    }


}
