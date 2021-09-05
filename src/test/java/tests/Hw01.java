package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVRecord;
import org.junit.BeforeClass;
import org.junit.Test;
import tests.model.Info;
import util.CsvUtil;
import util.IfThisTestFailsMaxPoints;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

public class Hw01 extends AbstractHw {

    private static Info info;

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
        assertThat(info.getFormOfStudy(), isOneOf("S", "O"));
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

    @Test
    @IfThisTestFailsMaxPoints(3)
    public void nameIsWrittenAsInOis() throws Exception {
        String fullName = fullName(info.getFirstName(), info.getLastName());

        if (!getDeclaredNames().contains(fullName)) {

            fail(String.format("There is no declaration with name '%s' in Õis (as of 05.09.2021)."
                    + " If you declared the course later and the name is correct you will get"
                    + " the points on 19.09.2021", fullName));
        }
    }

    private String fullName(String first, String last) {
        return first + " " + last;
    }

    private Set<String> getDeclaredNames() throws Exception {
        String[] headers = { "õppuri kood", "UNI-ID", "eesnimi", "perekonnanimi"};

        var loaded = CsvUtil.readCSVFile(
                new FileReader(arg1), headers, ';');

        Set<String> names = new HashSet<>();
        for (CSVRecord record : loaded) {
            names.add(fullName(record.get("eesnimi"), record.get("perekonnanimi")));
        }

        return names;
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
