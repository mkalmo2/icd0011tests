package util;

import javax.ws.rs.core.Cookie;
import java.util.Optional;

public class RequestResult {

    private Integer statusCode;
    private Optional<Cookie> cookie = Optional.empty();

}
