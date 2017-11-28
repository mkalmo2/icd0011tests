package util;

import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import tests.*;

public class Runner {

    public static void main(String[] args) {
        String tag = args[0];

        new Runner().run(tag);
    }

    private void run(String tag) {
        JUnitCore junit = new JUnitCore();
        final PointCounter counter = new PointCounter(10);
        junit.addListener(new RunListener() {
            @Override
            public void testFailure(Failure failure) throws Exception {
                PenaltyOnTestFailure penalty = failure.getDescription().getAnnotation(PenaltyOnTestFailure.class);
                counter.subtract(penalty.value());
                System.out.println("   " + failure.getDescription() + " failed");
            }
        });

        junit.run(resolveClass(tag));

        System.out.println("RESULT: " + counter.getResult() + " POINTS");
    }

    private Class<?> resolveClass(String tag) {
        switch (tag) {
            case "hw2" : return Hw2.class;
            case "hw3" : return Hw3.class;
            case "hw4" : return Hw4.class;
            case "hw5" : return Hw5.class;
            case "hw6" : return Hw6.class;
            default: throw new IllegalStateException("unknown tag: " + tag);
        }
    }

    private static class PointCounter {
        int count;

        public PointCounter(int max) {
            this.count = max;
        }

        public int getResult() {
            return Math.max(0, count);
        }

        public void subtract(int howMany) {
            count -= howMany;
        }
    }
}