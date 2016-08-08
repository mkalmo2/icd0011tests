package tests.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Customer {
    private Long id;
    private String firstName;
    private String lastName;
    private String type;
    private String code;

    private List<Phone> phones = new ArrayList<>();

    public Customer(Long id, String firstName, String lastName, String type, String code) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = type;
        this.code = code;
    }
}
