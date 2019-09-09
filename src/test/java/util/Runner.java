package util;

import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import tests.*;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

        PrintStream out = System.out;

        System.setOut(new PrintStream(new NullOutputStream()));

        final PointHolder pointHolder = new PointHolder(getMaxPoints(tag));

        JUnitCore junit = new JUnitCore();
        junit.addListener(new RunListener() {
            @Override
            public void testFailure(Failure failure) {

                IfThisTestFailsMaxPoints annotation = failure.getDescription()
                        .getAnnotation(IfThisTestFailsMaxPoints.class);

                if (annotation != null) {
                    pointHolder.capPointsTo(annotation.value());
                } else {
                    pointHolder.capPointsTo(0);
                }

                out.println("   " + failure.getDescription() + " failed");
                out.println(failure.getException());

                String trace = Arrays.asList(failure.getException().getStackTrace())
                        .stream()
                        .filter(f -> ! f.getClassName().startsWith("junit."))
                        .filter(f -> ! f.getClassName().startsWith("jdk."))
                        .filter(f -> ! f.getClassName().startsWith("java."))
                        .filter(f -> ! f.getClassName().startsWith("org."))
                        .filter(f -> ! f.getClassName().startsWith("util.Runner"))
                        .filter(f -> ! f.getClassName().startsWith("tests.AbstractHw"))
                        .map(f -> f.toString())
                        .collect(Collectors.joining("\n"));

                out.println(trace);

                out.println("\n\n");
            }
        });

        junit.run(resolveClass(tag));

        String pattern = "RESULT: {0} of {1} POINTS";

        out.println(MessageFormat.format(pattern,
                pointHolder.points, getMaxPoints(tag)));
    }

    private Integer getMaxPoints(String tag) {
        return tag.equals("hw1") ? 6 : 10;
    }

    private static Class<?> resolveClass(String tag) {
        if (!Arrays.asList(
                "hw1", "hw2", "hw3", "hw4", "hw5",
                "hw6", "hw7", "hw8", "hw9").contains(tag)) {

            throw new IllegalStateException("unknown tag: " + tag);
        }

        return loadClass("tests.Hw" + tag.charAt(2));
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

    private static class PointHolder {
        int points;

        PointHolder(int max) {
            this.points = max;
        }

        void capPointsTo(int newMax) {
            points = Math.min(points, newMax);
        }
    }

}