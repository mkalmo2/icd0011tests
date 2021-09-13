package util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


import static java.nio.file.FileVisitResult.CONTINUE;

public class FileFinder {

    public Collection<Path> getAllFilesFrom(Path startingDir,
                                            List<String> extensions) {

        FileVisitor visitor = new FileVisitor(extensions);

        try {
            Files.walkFileTree(startingDir, visitor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return visitor.getFilesFound();
    }

    public static class FileVisitor extends SimpleFileVisitor<Path> {

        private List<Path> found = new ArrayList<>();

        private List<String> extensions;

        public FileVisitor(List<String> extensions) {
            this.extensions = extensions;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {

            for (String ext : extensions) {
                if (file.getFileName().toFile().getName().endsWith(ext)) {
                    found.add(file);
                }
            }

            return CONTINUE;
        }

        public Collection<Path> getFilesFound() {
            return Collections.unmodifiableCollection(found);
        }
    }

}
