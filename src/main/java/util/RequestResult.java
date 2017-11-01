package util;

import lombok.Data;

import javax.ws.rs.core.Cookie;
import java.util.Optional;

@Data
public class RequestResult {

    private Integer statusCode;
    private Optional<Cookie> cookie = Optional.empty();

}
