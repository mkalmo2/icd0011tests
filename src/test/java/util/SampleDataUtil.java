package util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SampleDataUtil {

    public static Map<String, JsonValue> getRandomMap() {
        Map<String, JsonValue> map = new HashMap<>();

        int count = new Random().nextInt(5) + 8;

        for (int i = 0; i < count; i++) {
            map.put(getRandomString(2, 5), getRandomValue());
        }

        return map;
    }

    public static JsonValue getRandomValue() {
        Random random = new Random();
        return random.nextInt(2) > 0
                ? new JsonValue(getRandomString(2, 5))
                : new JsonValue(random.nextInt(5));
    }

    public static String getRandomString(int minLength, int maxLength) {
        Random random = new Random();

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < minLength + random.nextInt(maxLength - minLength + 1); i++) {
            int code = random.nextInt(23);
            result.append(Character.toChars(code + 'a'));
        }

        return result.toString();
    }


}
