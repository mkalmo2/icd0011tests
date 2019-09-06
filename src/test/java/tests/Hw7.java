package tests;

import org.junit.Test;
import util.PenaltyOnTestFailure;

import static org.junit.Assert.fail;

public class Hw7 {

    @Test
    @PenaltyOnTestFailure(10)
    public void missing() {
        fail("not ready yet");
    }

}
