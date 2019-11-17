package util;

import java.util.Optional;

public class RequestResult {

    private Integer statusCode;
    private Optional<String> authorization = Optional.empty();

    public Optional<String> getAuthorization() {
        return authorization;
    }

    public void setAuthorization(Optional<String> authorization) {
        this.authorization = authorization;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
}