package util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CsvUtil {

    public static List<CSVRecord> readCSVFile(Reader in,
                                              String[] headers,
                                              Character delimiter) throws IOException {

        Iterable<CSVRecord> records = CSVFormat.Builder.create()
                .setQuoteMode(QuoteMode.ALL)
                .setHeader(headers)
                .setDelimiter(';')
                .setSkipHeaderRecord(true)
                .build()
                .parse(in);

        return StreamSupport.stream(records.spliterator(), false)
                .collect(Collectors.toList());
    }
}
