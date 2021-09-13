package util;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;


public class FileReader {

    public static class File {
        public final String name;
        public final String contents;
        public File(String name, String contents) {
            this.name = name;
            this.contents = contents;
        }
    }

    public List<File> getAllFilesAndContentsFrom(
            Path startingDir, List<String> extensions) {

        Collection<Path> paths = new FileFinder()
                .getAllFilesFrom(startingDir, extensions);

        return paths.stream()
                .map(path -> new File(path.getFileName().toString(), readFile(path)))
                .collect(Collectors.toList());
    }

    private String readFile(Path path) {
        try {
            return Files.lines(path).collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
