package com.dpod.plcryptotaxcalc.nbp;

import com.dpod.plcryptotaxcalc.csv.NbpCsvIndexes;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static com.dpod.plcryptotaxcalc.Utils.createCsvReader;

public class NbpRates {

    private final LinkedHashMap<LocalDate, NbpDailyRates> rates;

    public NbpRates(String ratesFileYearBefore, String currencyRatesFile, int year) throws CsvValidationException, IOException {
        rates = readNbpRates(ratesFileYearBefore, currencyRatesFile, year);
    }

    static LinkedHashMap<LocalDate, NbpDailyRates> readNbpRates(String ratesFileYearBefore, String filename, int year)
            throws IOException, CsvValidationException {

        LinkedHashMap<LocalDate, NbpDailyRates> rates = new LinkedHashMap<>();
        prepopulateEmptyRates(rates, year);

        try (CSVReader csvReader = createCsvReader(filename, ';')) {
            String[] headers = csvReader.readNext();
            NbpCsvIndexes nbpCsvIndexes = new NbpCsvIndexes(headers);

            String[] values;
            while ((values = csvReader.readNext()) != null) {
                List<String> fields = Arrays.asList(values);
                String dateAsString = fields.get(nbpCsvIndexes.date());

                // skip rows without date (e.g. with column information)
                if (!StringUtils.isNumeric(dateAsString)) {
                    continue;
                }

                LocalDate date = LocalDate.parse(dateAsString, DateTimeFormatter.ofPattern("yyyyMMdd"));
                NbpDailyRates nbpDailyRates = new NbpDailyRates(date, fields.get(nbpCsvIndexes.usd()), fields.get(nbpCsvIndexes.eur()));
                rates.put(nbpDailyRates.getDate(), nbpDailyRates);
            }
        }

        try (CSVReader csvReader = createCsvReader(ratesFileYearBefore, ';')) {
            String[] headers = csvReader.readNext();
            NbpCsvIndexes nbpRatesCsvHeader = new NbpCsvIndexes(headers);

            String[] values;
            while ((values = csvReader.readNext()) != null) {
                List<String> fields = Arrays.asList(values);
                String date = fields.get(nbpRatesCsvHeader.date());

                // skip rows without date (e.g. with column information)
                if (!StringUtils.isNumeric(date)) {
                    continue;
                }

                // todo
                if (date.equals(year - 1 + "1214")) {
                    break;
                }
            }

            while ((values = csvReader.readNext()) != null) {
                List<String> fields = Arrays.asList(values);
                String dateAsString = fields.get(nbpRatesCsvHeader.date());

                // skip rows without date (e.g. with column information)
                if (!StringUtils.isNumeric(dateAsString)) {
                    continue;
                }

                LocalDate date = LocalDate.parse(dateAsString, DateTimeFormatter.ofPattern("yyyyMMdd"));
                NbpDailyRates nbpDailyRates = new NbpDailyRates(date, fields.get(nbpRatesCsvHeader.usd()), fields.get(nbpRatesCsvHeader.eur()));
                rates.put(nbpDailyRates.getDate(), nbpDailyRates);
            }
        }
        return rates;
    }

    // at NBP rates from the closest business day preceding transactions
    public NbpDailyRates findRateForPreviousBusinessDay(LocalDate test) {
        LocalDate previousDay = test.minusDays(1);
        NbpDailyRates nbpDailyRates = rates.get(previousDay);
        // todo refactor
        if (nbpDailyRates != null) {
            return nbpDailyRates;
        }
        do {
            previousDay = previousDay.minusDays(1);
        } while (rates.get(previousDay) == null);

        return rates.get(previousDay);
    }

    private static void prepopulateEmptyRates(LinkedHashMap<LocalDate, NbpDailyRates> rates, int year) {
        LocalDate localDate = LocalDate.of(year - 1, Month.DECEMBER, 15);
        LocalDate firstDayOfNextYear = LocalDate.of(year + 1, Month.JANUARY, 1);
        do {
            rates.put(localDate, null);
            localDate = localDate.plusDays(1);
        } while (localDate.isBefore(firstDayOfNextYear));
    }
}
