package tests.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ClassifierInfo {

    private List<String> customerTypes;
    private List<String> phoneTypes;

}
