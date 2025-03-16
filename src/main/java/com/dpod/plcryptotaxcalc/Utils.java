package com.dpod.plcryptotaxcalc;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.io.InputStreamReader;

@UtilityClass
public class Utils {

    public static CSVReader createCsvReader(String filename, char separator) {
        CSVParser csvParser = new CSVParserBuilder()
                .withSeparator(separator)
                .build();
        return new CSVReaderBuilder(new InputStreamReader(inputStream(filename)))
                .withCSVParser(csvParser)
                .build();
    }

    public static InputStream inputStream(String filename) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        return classloader.getResourceAsStream(filename);
    }
}
