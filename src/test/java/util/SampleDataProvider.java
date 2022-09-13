package util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SampleDataProvider {

    private final Random rnd = new Random();
    private final int maxDepth;

    private final String KEY_ALPHABET = "abcdefghijk";
    private final String VALUE_ALPHABET = KEY_ALPHABET + ":,{}";

    public SampleDataProvider(int maxDepth) {
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
            String key = getRandomString(2, 5, KEY_ALPHABET);

            JsonValue value = (i == mapValuePos && currentDepth <= maxDepth)
                    ? getRandomMap(currentDepth + 1)
                    : getRandomValue();

            map.put(key, value);
        }

        return new JsonValue(map);
    }

    private JsonValue getRandomValue() {
        return rnd.nextBoolean()
                ? new JsonValue(getRandomString(2, 5, VALUE_ALPHABET))
                : new JsonValue(rnd.nextInt(50));
    }

    public String getRandomString(int minLength, int maxLength, String alphabet) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < minLength + rnd.nextInt(maxLength - minLength + 1); i++) {
            int index = rnd.nextInt(alphabet.length());
            result.append(alphabet.charAt(index));
        }

        return result.toString();
    }
}
