package com.dpod.crypto.taxcalc.csv;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.experimental.UtilityClass;

import java.io.InputStreamReader;
import java.util.stream.IntStream;

import static com.dpod.crypto.taxcalc.util.FileUtils.opeInputStreamFor;

@UtilityClass
public class CsvUtils {

    public static int findIndexByName(String columnName, String[] headerLine) {
        return IntStream.range(0, headerLine.length)
                .filter(i -> columnName.equals(headerLine[i]))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("CSV file doesn't contain column " + columnName));
    }

    public static CSVReader createCsvReader(String filename, char separator) {
        CSVParser csvParser = new CSVParserBuilder()
                .withSeparator(separator)
                .build();
        return new CSVReaderBuilder(new InputStreamReader(opeInputStreamFor(filename)))
                .withCSVParser(csvParser)
                .build();
    }
}
