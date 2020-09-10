package util;

public class JsonValue {

    private String stringValue;
    private Integer integerValue;

    public JsonValue() {
    }

    public JsonValue(String value) {
        this.stringValue = value;
    }

    public JsonValue(Integer value) {
        this.integerValue = value;
    }

    public boolean isInteger() {
        return integerValue != null;
    }

    public boolean isString() {
        return stringValue != null;
    }

    public Integer getInteger() {
        return integerValue == null
                ? null
                : integerValue;
    }

    public String getString() {
        return stringValue == null
                ? null
                : stringValue;
    }

    public Object getValue() {
        return stringValue == null
                ? integerValue
                : stringValue;
    }

    public String formatValue() {
        if (isString()) {
            return "\"" + stringValue + "\"";
        } else if (isInteger()) {
            return String.valueOf(integerValue);
        } else {
            return "null";
        }
    }

    @Override
    public String toString() {
        return formatValue();
    }
}
