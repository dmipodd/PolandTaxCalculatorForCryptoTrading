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
import java.util.LinkedHashMap;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        // take nbp quotes from https://nbp.pl/statystyka-i-sprawozdawczosc/kursy/archiwum-tabela-a-csv-xls/
        String currencyRatesFile = "archiwum_tab_a_2024.csv";
        String transactionsFile = "TransactionsExport.csv";
        int year = 2024;

        NbpRates nbpRates = new NbpRates(currencyRatesFile, 2024);
        Bitstamp.bitstamp(nbpRates, transactionsFile);
//        LinkedHashMap<LocalDate, NbpRecord> rates = readNbpRates("nbp_quotes_2021.csv");
//        testRates(rates);
//        revolut(rates);
//        revolut2022(rates);
//        binance(rates);
//        bitstamp(rates, "bitstamp.csv");
    }

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
        InputStream is = openFile("dates_revolut_2022.csv");
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

    private static List<LocalDate> readDates() throws IOException, CsvValidationException {
        InputStream is = openFile("dates_revolut.csv");
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

    private static void testRates(NbpRates nbpRates) {
        //        20210304;3,7851;4,554
//        20210305;3,8393;4,5793
//        20210308;3,8665;4,5903
//        20210309;3,8507;4,5844
//        20210310;3,842;4,5718
//        20210311;3,8287;4,5805
//        20210312;3,8521;4,5909
//        20210315;3,8429;4,5836
        printUsdRate(nbpRates, LocalDate.of(2021, 3, 5));
        printUsdRate(nbpRates, LocalDate.of(2021, 3, 6));
        printUsdRate(nbpRates, LocalDate.of(2021, 3, 7));
        printUsdRate(nbpRates, LocalDate.of(2021, 3, 8));
        printUsdRate(nbpRates, LocalDate.of(2021, 3, 9));
        printUsdRate(nbpRates, LocalDate.of(2021, 3, 10));
        printUsdRate(nbpRates, LocalDate.of(2021, 3, 11));
        printUsdRate(nbpRates, LocalDate.of(2021, 3, 12));
        printUsdRate(nbpRates, LocalDate.of(2021, 3, 13));
        printUsdRate(nbpRates, LocalDate.of(2021, 3, 14));
        printUsdRate(nbpRates, LocalDate.of(2021, 3, 15));
        printUsdRate(nbpRates, LocalDate.of(2021, 3, 16));
    }

    private static void printUsdRate(NbpRates nbpRates, LocalDate localDate) {
        NbpRecord nbpRecord = nbpRates.findPreviousNbpDateRate(localDate);
        System.out.println(localDate + ": " + nbpRecord.usdRate);
    }

    static InputStream openFile(String filename) {
        String nbpFilename = filename;
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(nbpFilename);
        return is;
    }
}
