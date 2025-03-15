package com.dpod.plcryptotaxcalc;

import com.dpod.plcryptotaxcalc.csv.NbpRatesCsvIndexes;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static com.dpod.plcryptotaxcalc.Utils.createCsvReader;

public class NbpRates {

    private final LinkedHashMap<LocalDate, NbpRecord> rates;

    public NbpRates(String ratesFileYearBefore, String currencyRatesFile, int year) throws CsvValidationException, IOException {
        rates = readNbpRates(ratesFileYearBefore, currencyRatesFile, year);
    }

    static LinkedHashMap<LocalDate, NbpRecord> readNbpRates(String ratesFileYearBefore, String filename, int year)
            throws IOException, CsvValidationException {

        LinkedHashMap<LocalDate, NbpRecord> rates = new LinkedHashMap<>();
        prepopulateEmptyRates(rates, year);

        try (CSVReader csvReader = createCsvReader(filename, ';')) {
            String[] headers = csvReader.readNext();
            NbpRatesCsvIndexes nbpRatesCsvHeader = new NbpRatesCsvIndexes(headers);

            String[] values;
            while ((values = csvReader.readNext()) != null) {
                List<String> fields = Arrays.asList(values);
                String date = fields.get(nbpRatesCsvHeader.getDate());

                // skip rows without date (e.g. with column information)
                if (!StringUtils.isNumeric(date)) {
                    continue;
                }

                NbpRecord nbpRecord = new NbpRecord(date, fields.get(nbpRatesCsvHeader.getUsd()), fields.get(nbpRatesCsvHeader.getEur()));
                rates.put(nbpRecord.date, nbpRecord);
            }
        }

        try (CSVReader csvReader = createCsvReader(ratesFileYearBefore, ';')) {
            String[] headers = csvReader.readNext();
            NbpRatesCsvIndexes nbpRatesCsvHeader = new NbpRatesCsvIndexes(headers);

            String[] values;
            while ((values = csvReader.readNext()) != null) {
                List<String> fields = Arrays.asList(values);
                String date = fields.get(nbpRatesCsvHeader.getDate());

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
                String date = fields.get(nbpRatesCsvHeader.getDate());

                // skip rows without date (e.g. with column information)
                if (!StringUtils.isNumeric(date)) {
                    continue;
                }

                NbpRecord nbpRecord = new NbpRecord(date, fields.get(nbpRatesCsvHeader.getUsd()), fields.get(nbpRatesCsvHeader.getEur()));
                rates.put(nbpRecord.date, nbpRecord);
            }
        }
        return rates;
    }

    // at NBP rates from the closest business day preceding transactions
    NbpRecord findPreviousNbpDateRate(LocalDate test) {
        LocalDate previousDay = test.minusDays(1);
        NbpRecord nbpRecord = rates.get(previousDay);
        // todo refactor
        if (nbpRecord != NbpRecord.EMPTY) {
            return nbpRecord;
        }
        do {
            previousDay = previousDay.minusDays(1);
        } while (rates.get(previousDay).equals(NbpRecord.EMPTY));

        return rates.get(previousDay);
    }

    private static void prepopulateEmptyRates(LinkedHashMap<LocalDate, NbpRecord> rates, int year) {
        LocalDate localDate = LocalDate.of(year - 1, Month.DECEMBER, 15);
        LocalDate firstDayOfNextYear = LocalDate.of(year + 1, Month.JANUARY, 1);
        do {
            rates.put(localDate, NbpRecord.EMPTY);
            localDate = localDate.plusDays(1);
        } while (localDate.isBefore(firstDayOfNextYear));
    }
}
