package com.dpod.plcryptotaxcalc;

import com.dpod.plcryptotaxcalc.csv.BitstampCsvIndexes;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.dpod.plcryptotaxcalc.Utils.createCsvReader;

public class Bitstamp {

    static void bitstamp(NbpRates nbpRates, String filename)
            throws CsvValidationException, IOException {

        try (CSVReader csvReader = createCsvReader(filename, ',')) {
            String[] headers = csvReader.readNext();
            BitstampCsvIndexes bitstampCsvIndexes = new BitstampCsvIndexes(headers);
            calcTax(nbpRates, csvReader, bitstampCsvIndexes);
        }
    }

    private static void calcTax(NbpRates nbpRates, CSVReader csvReader, BitstampCsvIndexes bitstampCsvIndexes) throws IOException, CsvValidationException {
        String[] values;
        List<LocalDateWrapper> dates = new ArrayList<>();
        while ((values = csvReader.readNext()) != null) {
            List<String> fields = Arrays.asList(values);
            LocalDateWrapper wrapper = new LocalDateWrapper();
            String date = fields.get(bitstampCsvIndexes.getDateTime());
            String pair = fields.get(bitstampCsvIndexes.getCurrency());
            if (pair.endsWith("USD")) {
                wrapper.isUSD = true;
            } else if (pair.endsWith("EUR")) {
                wrapper.isUSD = false;
            } else {
                throw new IllegalStateException();
            }

            String dateAsString = date.substring(0, 13);
            wrapper.date = LocalDate.parse(dateAsString, DateTimeFormatter.ofPattern("MMM. dd, yyyy"));
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
