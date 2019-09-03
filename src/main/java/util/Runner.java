package util;

import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import tests.*;

import java.text.MessageFormat;
import java.util.Arrays;

public class Runner {

    public static void main(String[] args) {

        if (args.length < 1) {
            throw new RuntimeException("Provide tag as an argument");
        }

        String tag = args[0];

        if (args.length == 2) {
            Hw1.infoJsonPath = args[1];
            Hw7.pathToProjectSourceCode = args[1];
            Hw8.pathToProjectSourceCode = args[1];
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

    private Class<?> resolveClass(String tag) {
        switch (tag) {
            case "hw1" : return Hw1.class;
            case "hw2" : return Hw2.class;
            case "hw3" : return Hw3.class;
            case "hw4" : return Hw4.class;
            case "hw5" : return Hw5.class;
            case "hw6" : return Hw6.class;
            case "hw7" : return Hw7.class;
            case "hw8" : return Hw8.class;
            case "hw9" : return Hw9.class;
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