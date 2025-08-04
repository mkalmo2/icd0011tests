package util;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import tests.*;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Runner {

    public static void main(String[] args) {

        String tag;

        if (args.length < 1) {
            throw new RuntimeException("Provide tag as an argument");
        } else {
            tag = args[0];
        }

        if (args.length >= 2) {
            AbstractHw.setPathToSourceCode(args[1]);
        }

        if (args.length >= 3) {
            AbstractHw.setArg1(args[2]);
        }

        new Runner().run(tag);
    }

    public void run(String tag) {
        String className = resolveClassName(tag);

        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(DiscoverySelectors.selectClass(loadClass(className)))
                .configurationParameter("junit.jupiter.execution.parallel.enabled", "true")
                .build();

        Launcher launcher = LauncherFactory.create();

        PrintStream out = System.out;

        System.setOut(new PrintStream(OutputStream.nullOutputStream()));

        SummaryGeneratingListener listener = new SummaryGeneratingListener();

        launcher.registerTestExecutionListeners(listener);

        launcher.registerTestExecutionListeners(new TestExecutionListener() {
            @Override
            public void executionFinished(TestIdentifier testIdentifier,
                                          TestExecutionResult testExecutionResult) {

                testExecutionResult.getThrowable().ifPresent(t -> {
                    out.println(t.getMessage());
                    out.println(getTrace(t) + "\n");
                });
            }
        });

        launcher.execute(request);

        if (listener.getSummary().getTestsFailedCount() == 0) {
            out.println("RESULT: PASSED");
        } else {
            out.println("RESULT: FAILED");
        }

        System.setOut(out);
    }

    private String getTrace(Throwable throwable) {
        return Arrays.stream(throwable.getStackTrace())
                .filter(f -> ! f.getClassName().startsWith("jdk."))
                .filter(f -> ! f.getClassName().startsWith("java."))
                .filter(f -> ! f.getClassName().startsWith("org."))
                .filter(f -> ! f.getClassName().startsWith("runner."))
                .filter(f -> ! f.getClassName().startsWith("util.Runner"))
                .filter(f -> ! f.getClassName().startsWith("tests.AbstractHw"))
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
    }

    private static String resolveClassName(String tag) {
        if (!List.of(
                "hw01a", "hw01b", "hw02", "hw03", "hw03a", "hw04", "hw05", "hw05a",
                "hw06", "hw06a", "hw07", "hw07a", "hw08",
                "hw09", "hw10", "hw10a").contains(tag)) {

            throw new IllegalStateException("unknown tag: " + tag);
        }

        return "tests.Hw" + tag.substring(2);
    }

    private static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}