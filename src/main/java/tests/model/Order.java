package tests.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Order {
    private Long id;
    private String orderNumber;

    public Order(String orderNumber) {
        this.orderNumber = orderNumber;
    }

}
