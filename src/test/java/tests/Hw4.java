package tests;

import org.junit.Test;
import util.PenaltyOnTestFailure;

import static org.junit.Assert.fail;

public class Hw4 {

    @Test
    @PenaltyOnTestFailure(10)
    public void missing() {
        fail("not ready yet");
    }

}
