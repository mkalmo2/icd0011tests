package tests;

import org.apache.commons.csv.CSVRecord;
import org.junit.Test;
import util.CsvUtil;

import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.fail;

public class Hw01b extends Hw01a {

    @Test
    public void nameIsWrittenAsInOis() throws Exception {
        String fullName = fullName(info.getFirstName(), info.getLastName());

        if (!getDeclaredNames().contains(fullName)) {

            fail(String.format("There is no declaration with the name '%s' in Õis (as of 01.09.2024)."
                    + " If you declared the course later and the name is correct you will get"
                    + " the points on 18.09.2024", fullName));
        }
    }

    private String fullName(String first, String last) {
        return (first + " " + last).trim();
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
}
