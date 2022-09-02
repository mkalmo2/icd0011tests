package tests;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class Hw02 extends AbstractHw {

    @Test
    public void servletProducesOutput() {
        String response = getResponseAsString("/hello");

        assertThat(response, is("{}"));
    }

    @Override
    protected String getBaseUrl() {
        return "http://localhost:8080";
    }

}
