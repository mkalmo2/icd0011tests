package tests;

import util.FileFinder;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class CommonChecks {

    public static void main(String[] args) {

        if (args.length < 1) {
            throw new RuntimeException("Provide source directory as an argument");
        }

        new CommonChecks().repositoryShouldNotContainDerivedFiles(args[0]);
    }

    private void repositoryShouldNotContainDerivedFiles(String sourcePath) {

        String fileList = new FileFinder()
                .getAllFilesFrom(Paths.get(sourcePath), List.of(".class", ".jar", ".war"))
                .stream()
                .map(path -> path.getFileName().toString())
                .filter(fileName -> !fileName.equals("gradle-wrapper.jar"))
                .collect(Collectors.joining(", "));

        if ("".equals(fileList)) {
            return;
        }

        String message = "Project repository should not contain files with extensions:" +
                " class, jar or war but there are: " + fileList;

        throw new AssertionError(message);
    }
}
