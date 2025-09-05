package tests;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Hw02 extends AbstractHw {

    private final String baseUrl = "http://localhost:8080";

    @Test
    public void servletProducesOutput() {
        String response = getResponseAsString("/hello");

        assertThat(response).isEqualTo("{}");
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }

}
