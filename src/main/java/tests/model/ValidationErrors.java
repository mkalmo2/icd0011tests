package tests.model;

import lombok.Data;

import java.util.List;

@Data
public class ValidationErrors {

    private List<ValidationError> errors;

}
