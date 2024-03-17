package com.dpod;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        // take nbp quotes from https://nbp.pl/statystyka-i-sprawozdawczosc/kursy/archiwum-tabela-a-csv-xls/
        LinkedHashMap<LocalDate, NbpRecord> rates = readNbpRates(
                "nbp_quotes_2023.csv",
                2023);
//        LinkedHashMap<LocalDate, NbpRecord> rates = readNbpRates("nbp_quotes_2021.csv");
//        testRates(rates);
//        revolut(rates);
//        revolut2022(rates);
//        binance(rates);
//        bitstamp(rates, "bitstamp.csv");
        bitstamp(rates, "bitstamp 2023.csv");


    }

    private static void bitstamp(LinkedHashMap<LocalDate, NbpRecord> rates, String filename) throws CsvValidationException, IOException {
        InputStream is = openFile(filename);
        List<String> records = new ArrayList<>();
        CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(is))
                .withCSVParser(new CSVParserBuilder()
                        .withSeparator(';')
                        .build()).build();


        String[] values;
        List<LocalDateWrapper> dates = new ArrayList<>();
        while ((values = csvReader.readNext()) != null) {
            List<String> fields = Arrays.asList(values);
            LocalDateWrapper wrapper = new LocalDateWrapper();
            String date = fields.get(0);
            String pair = fields.get(1);
            if (pair.endsWith("USD")) {
                wrapper.isUSD = true;
            } else if (pair.endsWith("EUR")) {
                wrapper.isUSD = false;
            } else {
                throw new IllegalStateException();
            }

            String dateAsString = date.substring(0, 13);
            records.add(dateAsString);
            wrapper.date = LocalDate.parse(dateAsString, DateTimeFormatter.ofPattern("MMM. dd, yyyy"));
            dates.add(wrapper);
        }
        dates.stream()
                .map(localDate -> {
                    NbpRecord nbpRecord = findPreviousNbpDateRate(rates, localDate.date);
                    nbpRecord.isUSD = localDate.isUSD;
                    return nbpRecord;
                })
                .map(nbpRecord -> nbpRecord.isUSD ? nbpRecord.usdRate : nbpRecord.eurRate)
                .forEach(rate -> System.out.println(rate));
    }

    private static void binance(LinkedHashMap<LocalDate, NbpRecord> rates) throws IOException, CsvValidationException {
        InputStream is = openFile("binance.csv");
        List<String> records = new ArrayList<>();
        CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(is))
                .withCSVParser(new CSVParserBuilder()
                        .withSeparator(';')
                        .build()).build();


        String[] values;
        List<LocalDateWrapper> dates = new ArrayList<>();
        while ((values = csvReader.readNext()) != null) {
            List<String> fields = Arrays.asList(values);
            LocalDateWrapper wrapper = new LocalDateWrapper();
            String date = fields.get(0);
            String pair = fields.get(1);
            if (pair.endsWith("USDT")) {
                wrapper.isUSD = true;
            } else if (pair.endsWith("EUR")) {
                wrapper.isUSD = false;
            } else {
                throw new IllegalStateException();
            }

            String dateAsString = date.substring(0, 10);
            records.add(dateAsString);
            wrapper.date = LocalDate.parse(dateAsString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            dates.add(wrapper);
        }
        dates.stream()
                .map(localDate -> {
                    NbpRecord nbpRecord = findPreviousNbpDateRate(rates, localDate.date);
                    nbpRecord.isUSD = localDate.isUSD;
                    return nbpRecord;
                })
                .map(nbpRecord -> nbpRecord.isUSD ? nbpRecord.usdRate : nbpRecord.eurRate)
                .forEach(rate -> System.out.println(rate));
    }

    private static void revolut(LinkedHashMap<LocalDate, NbpRecord> rates) throws IOException, CsvValidationException {
        List<LocalDate> dates = readDates();
        dates.stream()
                .map(localDate -> findPreviousNbpDateRate(rates, localDate))
                .map(nbpRecord -> nbpRecord.usdRate)
                .forEach(usdRate -> System.out.println(usdRate));
    }

    private static void revolut2022(LinkedHashMap<LocalDate, NbpRecord> rates) throws IOException, CsvValidationException {
        List<LocalDate> dates = readDatesRevolut2022();
        dates.stream()
                .map(localDate -> findPreviousNbpDateRate(rates, localDate))
                .map(nbpRecord -> nbpRecord.usdRate)
                .forEach(usdRate -> System.out.println(usdRate));
    }

    private static List<LocalDate> readDatesRevolut2022() throws IOException, CsvValidationException {
        InputStream is = openFile("dates_revolut_2022.csv");
        List<String> records = new ArrayList<>();
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
            records.add(dateAsString);
            dates.add(LocalDate.parse(dateAsString, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        return dates;
    }

    private static List<LocalDate> readDates() throws IOException, CsvValidationException {
        InputStream is = openFile("dates_revolut.csv");
        List<String> records = new ArrayList<>();
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
            records.add(dateAsString);
            dates.add(LocalDate.parse(dateAsString, DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        }
        return dates;
    }

    private static void testRates(LinkedHashMap<LocalDate, NbpRecord> rates) {
        //        20210304;3,7851;4,554
//        20210305;3,8393;4,5793
//        20210308;3,8665;4,5903
//        20210309;3,8507;4,5844
//        20210310;3,842;4,5718
//        20210311;3,8287;4,5805
//        20210312;3,8521;4,5909
//        20210315;3,8429;4,5836
        printUsdRate(rates, LocalDate.of(2021, 3, 5));
        printUsdRate(rates, LocalDate.of(2021, 3, 6));
        printUsdRate(rates, LocalDate.of(2021, 3, 7));
        printUsdRate(rates, LocalDate.of(2021, 3, 8));
        printUsdRate(rates, LocalDate.of(2021, 3, 9));
        printUsdRate(rates, LocalDate.of(2021, 3, 10));
        printUsdRate(rates, LocalDate.of(2021, 3, 11));
        printUsdRate(rates, LocalDate.of(2021, 3, 12));
        printUsdRate(rates, LocalDate.of(2021, 3, 13));
        printUsdRate(rates, LocalDate.of(2021, 3, 14));
        printUsdRate(rates, LocalDate.of(2021, 3, 15));
        printUsdRate(rates, LocalDate.of(2021, 3, 16));
    }

    private static void printUsdRate(LinkedHashMap<LocalDate, NbpRecord> rates, LocalDate localDate) {
        NbpRecord nbpRecord = findPreviousNbpDateRate(rates, localDate);
        System.out.println(localDate + ": " + nbpRecord.usdRate);
    }

    private static NbpRecord findPreviousNbpDateRate(LinkedHashMap<LocalDate, NbpRecord> rates, LocalDate test) {
        LocalDate previousDay = test.minusDays(1);
        NbpRecord nbpRecord = rates.get(previousDay);
        if (nbpRecord != NbpRecord.EMPTY) {
            return nbpRecord;
        }
        do {
            previousDay = previousDay.minusDays(1);
        } while (rates.get(previousDay).equals(NbpRecord.EMPTY));

        return rates.get(previousDay);
    }

    private static LinkedHashMap<LocalDate, NbpRecord> readNbpRates(String filename, int year) throws IOException, CsvValidationException {
        LinkedHashMap<LocalDate, NbpRecord> rates = new LinkedHashMap<>();
        prepopulateEmptyRates(rates, year);

        InputStream is = openFile(filename);
        List<List<String>> records = new ArrayList<>();
        CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(is))
                .withCSVParser(new CSVParserBuilder()
                        .withSeparator(';')
                        .build()).build();


        String[] values;
        while ((values = csvReader.readNext()) != null) {
            List<String> fields = Arrays.asList(values);
            String date = fields.get(0);
            if (date.contains("data")) {
                continue;
            }

            NbpRecord nbpRecord = new NbpRecord(date, fields.get(1), fields.get(2));
            rates.put(nbpRecord.date, nbpRecord);
            records.add(fields);
        }

        return rates;
    }

    private static InputStream openFile(String filename) {
        String nbpFilename =  filename;
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(nbpFilename);
        return is;
    }

    private static void prepopulateEmptyRates(LinkedHashMap<LocalDate, NbpRecord> rates, int year) {
        LocalDate localDate = LocalDate.of(year, Month.JANUARY, 1);
        LocalDate endOfYear = LocalDate.of(year, Month.DECEMBER, 31);
        while (!localDate.equals(endOfYear)) {
            rates.put(localDate, NbpRecord.EMPTY);
            localDate = localDate.plusDays(1);
        }
    }
}
