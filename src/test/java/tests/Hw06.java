package tests;

import org.junit.Test;

import static org.junit.Assert.fail;


public class Hw06 extends AbstractHw {

    @Test
    public void notReadyYet() {
        fail("not ready yet");
    }

    @Override
    protected String getBaseUrl() {
        throw new RuntimeException("not implemented");
    }

}
