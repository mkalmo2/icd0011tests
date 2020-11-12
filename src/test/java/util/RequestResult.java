package util;

public class RequestResult {

    private Integer statusCode;
    private String authorization;
    private String contents;

    public String getAuthorization() {
        return authorization;
    }

    public RequestResult withAuthorization(String authorization) {
        this.authorization = authorization;
        return this;
    }

    public RequestResult withContents(String contents) {
        this.contents = contents;
        return this;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getContents() {
        return contents;
    }

    public RequestResult withStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
        return this;
    }
}