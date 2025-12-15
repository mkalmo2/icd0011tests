package tests;

import org.junit.jupiter.api.Test;
import util.FileFinder;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

public class Hw10a extends AbstractHw {

    private final String pathToProjectSourceCode = "";

    @Test
    public void repositoryShouldNotContainJavaFiles() {
        assumeProjectSourcePathIsSet(pathToProjectSourceCode);

        Path sourcePath = resolveProjectSourcePath(pathToProjectSourceCode);

        String fileList = new FileFinder()
                .getAllFilesFrom(sourcePath, List.of(".java"))
                .stream()
                .map(path -> path.getFileName().toString())
                .collect(Collectors.joining(", "));

        if (fileList.isEmpty()) {
            return;
        }

        String message = "Project repository should not contain Java files" +
                " but there are: " + fileList;

        fail(message);
    }

    @Override
    protected String getBaseUrl() {
        return "";
    }
}
