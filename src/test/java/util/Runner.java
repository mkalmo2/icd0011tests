package util;

import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import tests.*;

import java.text.MessageFormat;

public class Runner {

    public static void main(String[] args) {

        if (args.length < 1) {
            throw new RuntimeException("Provide tag as an argument");
        }

        String tag = args[0];

        if (args.length == 2) {
            AbstractHw.setPathToSourceCode(args[1]);
        }

        new Runner().run(tag);
    }

    private void run(String tag) {
        Integer maxPoints = getMaxPoints(tag);

        JUnitCore junit = new JUnitCore();
        final PointCounter counter = new PointCounter(maxPoints);
        junit.addListener(new RunListener() {
            @Override
            public void testFailure(Failure failure) throws Exception {
                if (failure.getException().getClass() != AssertionError.class) {
                    counter.subtract(maxPoints);
                    System.out.println(failure.getException());
                    return;
                }

                PenaltyOnTestFailure penalty = failure.getDescription().getAnnotation(PenaltyOnTestFailure.class);
                counter.subtract(penalty.value());
                System.out.println("   " + failure.getDescription() + " failed");
            }
        });

        junit.run(resolveClass(tag));

        String pattern = "RESULT: {0} of {1} POINTS";

        System.out.println(MessageFormat.format(pattern,
                counter.getResult(), maxPoints));
    }

    private Integer getMaxPoints(String tag) {
        return tag.equals("hw1") ? 6 : 10;
    }

    private static Class<?> resolveClass(String tag) {
        switch (tag) {
            case "hw1" : return loadClass("tests.Hw1");
            case "hw2" : return loadClass("tests.Hw2");
            case "hw3" : return loadClass("tests.Hw3");
            case "hw4" : return loadClass("tests.Hw4");
            case "hw5" : return loadClass("tests.Hw5");
            case "hw6" : return loadClass("tests.Hw6");
            case "hw7" : return loadClass("tests.Hw7");
            case "hw8" : return loadClass("tests.Hw8");
            case "hw9" : return loadClass("tests.Hw9");
            default: throw new IllegalStateException("unknown tag: " + tag);
        }
    }

    private static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
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