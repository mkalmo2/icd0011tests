package tests;

import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import util.CsvUtil;

import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class Hw01b extends Hw01a {

    @Test
    public void nameIsWrittenAsInOis() throws Exception {
        String fullName = fullName(info.getFirstName(), info.getLastName());

        assertThat(getDeclaredNames())
                .withFailMessage("There is no declaration with the name '%s' in Õis (as of 31.08.2025)."
                        + " If you declared the course later and the name is correct you will get"
                        + " the points on 18.09.2025", fullName)
                .contains(fullName);
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
            names.add(fullName(record.get("eesnimi").trim(),
                               record.get("perekonnanimi").trim()));
        }

        return names;
    }
}
