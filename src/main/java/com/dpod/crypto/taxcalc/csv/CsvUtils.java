package com.dpod.crypto.taxcalc.csv;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.IntStream;

@UtilityClass
public class CsvUtils {

    public static int findIndexByName(String columnName, String[] csvHeaderRow) {
        return IntStream.range(0, csvHeaderRow.length)
                .filter(i -> columnName.equals(csvHeaderRow[i]))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("CSV file doesn't contain column " + columnName));
    }

    public static CSVReader createCsvReader(String filename, char separator) {
        CSVParser csvParser = new CSVParserBuilder()
                .withSeparator(separator)
                .build();
        return new CSVReaderBuilder(new InputStreamReader(inputStream(filename)))
                .withCSVParser(csvParser)
                .build();
    }

    private static InputStream inputStream(String filename) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        return classloader.getResourceAsStream(filename);
    }
}
