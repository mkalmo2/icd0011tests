package tests;

import util.FileFinder;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;

public class CommonChecks {

    public static void main(String[] args) {

        if (args.length < 1) {
            throw new RuntimeException("Provide source directory as an argument");
        }

        new CommonChecks().shouldUseSpringInsteadOfLowerLeverClasses(args[0]);
    }

    private void shouldUseSpringInsteadOfLowerLeverClasses(String sourcePath) {

        String fileList = new FileFinder()
                .getAllFilesFrom(Paths.get(sourcePath), List.of(".class", ".jar", ".war"))
                .stream()
                .map(path -> path.getFileName().toString())
                .filter(fileName -> !fileName.equals("gradle-wrapper.jar"))
                .collect(Collectors.joining(", "));

        assertThat("Project repository should not contain files with extensions:" +
                        " class, jar or war",
                fileList, is(emptyString()));
    }

}
