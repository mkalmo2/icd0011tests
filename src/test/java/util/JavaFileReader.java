package util;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.file.FileVisitResult.CONTINUE;

public class JavaFileReader {

    public String getAllFilesFrom(Path startingDir) {
        JavaFileFinder finder = new JavaFileFinder();

        try {
            Files.walkFileTree(startingDir, finder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return finder.getFilesFound().stream()
                .map(path -> readFile(path))
                .collect(Collectors.joining("\n"));
    }

    private String readFile(Path path) {
        try {
            return Files.lines(path).collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static class JavaFileFinder extends SimpleFileVisitor<Path> {

        private List<Path> javaFiles = new ArrayList<>();

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {

            if (file.getFileName().toFile().getName().endsWith(".java")) {
                javaFiles.add(file);
            }

            return CONTINUE;
        }

        public Collection<Path> getFilesFound() {
            return Collections.unmodifiableCollection(javaFiles);
        }
    }

}
