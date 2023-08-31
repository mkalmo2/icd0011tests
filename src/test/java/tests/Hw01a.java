package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Test;
import tests.model.Info;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class Hw01a extends AbstractHw {

    protected static Info info;

    @BeforeClass
    public static void prepare() throws IOException {
        if (frameworkPathToSourceCode.isEmpty()) {
            throw new RuntimeException("Provide project source path as a second argument");
        }

        String contents = getFileContents("info.json");

        info = new ObjectMapper().readValue(contents, Info.class);
    }

    @Test
    public void infoJsonFileExists() {
        assertThat(info, is(notNullValue()));
    }

    @Test
    public void firstNameFieldIsFilled() {
        assertThat(info.getFirstName().length(), is(greaterThan(1)));
    }

    @Test
    public void lastNameFieldIsFilled() {
        assertThat(info.getLastName().length(), is(greaterThan(1)));
    }

    @Test
    public void formOfStudyIsValid() {
        assertThat(info.getFormOfStudy(), oneOf("S", "O"));
    }

    @Test
    public void readTheRulesIsMarkedTrue() {
        assertThat(info.isiHaveReadTheRulesOfTheCourse(), is(true));
    }

    @Test
    public void projectContainsCorrectGitIgnoreFile() {
        String content = getFileContents(".gitignore");

        assertThat(content, containsString("*.class"));
        assertThat(content, containsString("*.war"));
        assertThat(content, containsString("*.jar"));
    }

    private static String getFileContents(String fileName) {
        Path path = Paths.get(frameworkPathToSourceCode, fileName);

        try {
            return String.join("\n", Files.readAllLines(path));
        } catch (IOException e) {
            throw new AssertionError("Can not find file: " + path);
        }
    }

    @Override
    protected String getBaseUrl() {
        throw new RuntimeException("not implemented");
    }
}
