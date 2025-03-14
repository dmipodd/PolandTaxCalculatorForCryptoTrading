package com.dpod.plcryptotaxcalc;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.dpod.plcryptotaxcalc.Utils.inputStream;

public class Revolut {
    private static void revolut(NbpRates nbpRates) throws IOException, CsvValidationException {
        List<LocalDate> dates = readDates();
        dates.stream()
                .map(localDate -> nbpRates.findPreviousNbpDateRate(localDate))
                .map(nbpRecord -> nbpRecord.usdRate)
                .forEach(usdRate -> System.out.println(usdRate));
    }

    private static void revolut2022(NbpRates nbpRates) throws IOException, CsvValidationException {
        List<LocalDate> dates = readDatesRevolut2022();
        dates.stream()
                .map(localDate -> nbpRates.findPreviousNbpDateRate(localDate))
                .map(nbpRecord -> nbpRecord.usdRate)
                .forEach(usdRate -> System.out.println(usdRate));
    }

    private static List<LocalDate> readDatesRevolut2022() throws IOException, CsvValidationException {
        InputStream is = inputStream("dates_revolut_2022.csv");
        CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(is))
                .withCSVParser(new CSVParserBuilder()
                        .withSeparator(';')
                        .build()).build();


        String[] values;
        List<LocalDate> dates = new ArrayList<>();
        while ((values = csvReader.readNext()) != null) {
            List<String> fields = Arrays.asList(values);
            String date = fields.get(0);
            String dateAsString = date.substring(0, 10);
            dates.add(LocalDate.parse(dateAsString, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        return dates;
    }

    static List<LocalDate> readDates() throws IOException, CsvValidationException {
        InputStream is = inputStream("dates_revolut.csv");
        CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(is))
                .withCSVParser(new CSVParserBuilder()
                        .withSeparator(';')
                        .build()).build();


        String[] values;
        List<LocalDate> dates = new ArrayList<>();
        while ((values = csvReader.readNext()) != null) {
            List<String> fields = Arrays.asList(values);
            String date = fields.get(0);
            String dateAsString = date.substring(0, 10);
            dates.add(LocalDate.parse(dateAsString, DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        }
        return dates;
    }
}
