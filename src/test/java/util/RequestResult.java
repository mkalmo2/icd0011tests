package util;

import javax.ws.rs.core.Cookie;
import java.util.Optional;

public class RequestResult {

    private Integer statusCode;
    private Optional<Cookie> cookie = Optional.empty();

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Optional<Cookie> getCookie() {
        return cookie;
    }

    public void setCookie(Optional<Cookie> cookie) {
        this.cookie = cookie;
    }
}