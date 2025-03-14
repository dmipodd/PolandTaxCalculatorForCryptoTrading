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

import static com.dpod.plcryptotaxcalc.Utils.openFile;

public class Binance {

    private static void binance(NbpRates nbpRates) throws IOException, CsvValidationException {
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
                    NbpRecord nbpRecord = nbpRates.findPreviousNbpDateRate(localDate.date);
                    nbpRecord.isUSD = localDate.isUSD;
                    return nbpRecord;
                })
                .map(nbpRecord -> nbpRecord.isUSD ? nbpRecord.usdRate : nbpRecord.eurRate)
                .forEach(rate -> System.out.println(rate));
    }
}
