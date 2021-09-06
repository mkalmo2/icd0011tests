package tests;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class Hw02 extends AbstractHw {

    private final String baseUrl = "http://localhost:8080";

    @Test
    public void servletProducesOutput() {
        String response = getResponseAsString("/hello");

        assertThat(response, is("{}"));
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }

}
