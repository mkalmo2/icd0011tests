package util;

import java.util.HashMap;
import java.util.Map;

public class JsonValue {

    private String stringValue;
    private Integer integerValue;
    private Map<String, JsonValue> mapValue;

    public JsonValue(String value) {
        this.stringValue = value;
    }

    public JsonValue(Integer value) {
        this.integerValue = value;
    }

    public JsonValue(Map<String, JsonValue> value) {
        this.mapValue = value;
    }

    public boolean isInteger() {
        return integerValue != null;
    }

    public boolean isMap() {
        return mapValue != null;
    }

    public boolean isString() {
        return stringValue != null;
    }

    public Integer getInteger() {
        return getChecked(integerValue);
    }

    public void setInteger(Integer value) {
        integerValue = value;
    }

    public Map<String, JsonValue> getMap() {
        return getChecked(mapValue);
    }

    public String getString() {
        return getChecked(stringValue);
    }

    private <T> T getChecked(T value) {
        if (value == null) {
            throw new RuntimeException("value is null");
        }

        return value;
    }

    public Object getValue() {
        if (stringValue != null) {
            return stringValue;
        } else if (integerValue != null) {
            return integerValue;
        } else if (mapValue != null) {
            return toObjectMap(mapValue);
        } else {
            throw new RuntimeException("unexpected");
        }
    }

    public Map<String, Object> toObjectMap(
            Map<String, JsonValue> inputMap) {

        Map<String, Object> outputMap = new HashMap<>();

        for (String key : inputMap.keySet()) {

            JsonValue value = inputMap.get(key);

            if (value.isString()) {
                outputMap.put(key, value.getString());
            } else if (value.isInteger()) {
                outputMap.put(key, value.getInteger());
            } else if (value.isMap()) {
                outputMap.put(key, toObjectMap(value.getMap()));
            } else {
                throw new IllegalArgumentException("unexpected type");
            }
        }

        return outputMap;
    }

    public String formatValue() {
        if (isString()) {
            return "\"" + stringValue + "\"";
        } else if (isInteger()) {
            return String.valueOf(integerValue);
        } else {
            return getValue().toString();
        }
    }

    @Override
    public String toString() {
        return formatValue();
    }
}
