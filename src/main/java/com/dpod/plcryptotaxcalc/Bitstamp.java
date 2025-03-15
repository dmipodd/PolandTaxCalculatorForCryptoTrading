package com.dpod.plcryptotaxcalc;

import com.dpod.plcryptotaxcalc.csv.BitstampCsvIndexes;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.dpod.plcryptotaxcalc.Utils.createCsvReader;

public class Bitstamp {

    static void bitstamp(NbpRates nbpRates, String filename)
            throws CsvValidationException, IOException {

        try (CSVReader csvReader = createCsvReader(filename, ',')) {
            String[] headers = csvReader.readNext();
            var bitstampCsvIndexes = new BitstampCsvIndexes(headers);
            calcTax(nbpRates, csvReader, bitstampCsvIndexes);
        }
    }

    private static void calcTax(NbpRates nbpRates, CSVReader csvReader, BitstampCsvIndexes bitstampCsvIndexes) throws IOException, CsvValidationException {
        String[] values;
        List<LocalDateWrapper> dates = new ArrayList<>();
        while ((values = csvReader.readNext()) != null) {

            // 2024-01-02T11:52:53Z
            String dateTime = values[bitstampCsvIndexes.getDateTime()];
            ZonedDateTime utcZonedDateTime = ZonedDateTime.parse(dateTime);
            ZonedDateTime warsawZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.of("Europe/Warsaw"));
            LocalDate localDate = warsawZonedDateTime.toLocalDate();

            Currency currency = Currency.valueOf(values[bitstampCsvIndexes.getCurrency()]);
            NbpRecord nbpRecord = nbpRates.findPreviousNbpDateRate(localDate);
            nbpRecord.getRateFor(currency);

//            List<String> fields = Arrays.asList(values);
//            LocalDateWrapper wrapper = new LocalDateWrapper();
//            String date = fields.get(bitstampCsvIndexes.getDateTime());
//            String currency = fields.get(bitstampCsvIndexes.getCurrency());
//            if (currency.endsWith("USD")) {
//                wrapper.isUSD = true;
//            } else if (currency.endsWith("EUR")) {
//                wrapper.isUSD = false;
//            } else {
//                throw new IllegalStateException();
//            }
//
//            String dateAsString = date.substring(0, 13);
//            wrapper.date = LocalDate.parse(dateAsString, DateTimeFormatter.ofPattern("MMM. dd, yyyy"));
//            dates.add(wrapper);
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
