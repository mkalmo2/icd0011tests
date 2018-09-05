package tests;

import org.junit.Test;
import util.PenaltyOnTestFailure;

import static org.junit.Assert.fail;


public class Hw8 extends AbstractHw {

    private final String BASE_URL = "http://localhost:8080/";

    @Test
    @PenaltyOnTestFailure(10)
    public void notImplementedYet() {
        fail("Not implemented yet");
    }

    @Override
    protected String getBaseUrl() {
        return BASE_URL;
    }

}
