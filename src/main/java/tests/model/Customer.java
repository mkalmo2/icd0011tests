package tests.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Customer {
    private Long id;
    private String firstName;
    private String lastName;
    private String type;
    private String code;

    private List<Phone> phones;

    public Customer(Long id, String firstName, String lastName, String type, String code) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = type;
        this.code = code;
    }
}
