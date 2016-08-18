package tests.model;

import lombok.*;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter
public class Phone {

    private Long id;

    @NonNull
    private String type;

    @NonNull
    private String value;

}
