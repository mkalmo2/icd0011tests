package tests;

import org.junit.Test;
import util.PenaltyOnTestFailure;

import static org.junit.Assert.fail;


public class Hw4 extends AbstractHw {

    private final String BASE_URL = "http://localhost:8080/";

    @Test
    @PenaltyOnTestFailure(10)
    public void notImplemented() {
        fail("not implemented");
    }

    @Override
    protected String getBaseUrl() {
        return BASE_URL;
    }
}
