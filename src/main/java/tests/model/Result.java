package tests.model;

import java.util.ArrayList;
import java.util.List;

public class Result <T> {
    private boolean isSuccess;
    private T value;
    private List<ValidationError> errors = new ArrayList<>();

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setValue(T value) {
        isSuccess = true;
        this.value = value;
    }

    public void setErrors(List<ValidationError> errors) {
        isSuccess = false;
        this.errors = errors;
    }

    public T getValue() {
        return value;
    }

    public List<ValidationError> getErrors() {
        return new ArrayList<>(errors);
    }

}
