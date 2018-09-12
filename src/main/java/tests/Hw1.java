package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Test;
import tests.model.Info;
import util.PenaltyOnTestFailure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class Hw1 {

    public static String infoJsonPath;

    private static Info info;

    @BeforeClass
    public static void prepare() throws IOException {
        Path path = Paths.get(infoJsonPath, "info.json");

        info = new ObjectMapper().readValue(Files.newInputStream(path), Info.class);
    }

    @Test
    @PenaltyOnTestFailure(6)
    public void infoJsonFileExists() {
        assertThat(info, is(notNullValue()));
    }

    @Test
    @PenaltyOnTestFailure(6)
    public void firstNameFieldIsFilled() {
        assertThat(info.getFirstName().length(), is(greaterThan(1)));
    }

    @Test
    @PenaltyOnTestFailure(6)
    public void lastNameFieldIsFilled() {
        assertThat(info.getLastName().length(), is(greaterThan(1)));
    }

    @Test
    @PenaltyOnTestFailure(6)
    public void formOfStudyIsValid() {
        assertThat(info.getFormOfStudy(), isOneOf("S", "O"));
    }

    @Test
    @PenaltyOnTestFailure(3)
    public void uniIdFieldIsFilled() {
        assertThat(info.getUniId().length(), is(greaterThan(1)));
    }

}
