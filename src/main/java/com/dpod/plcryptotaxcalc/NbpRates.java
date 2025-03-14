package com.dpod.plcryptotaxcalc;

import com.dpod.plcryptotaxcalc.csv.NbpRatesCsvIndexes;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static com.dpod.plcryptotaxcalc.Utils.inputStream;

public class NbpRates {

    private final LinkedHashMap<LocalDate, NbpRecord> rates;

    public NbpRates(String currencyRatesFile, int year) throws CsvValidationException, IOException {
        rates = readNbpRates(currencyRatesFile, year);
    }

    static LinkedHashMap<LocalDate, NbpRecord> readNbpRates(String filename, int year)
            throws IOException, CsvValidationException {

        LinkedHashMap<LocalDate, NbpRecord> rates = new LinkedHashMap<>();
        prepopulateEmptyRates(rates, year);

        InputStream is = inputStream(filename);
        CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(is))
                .withCSVParser(new CSVParserBuilder()
                        .withSeparator(';')
                        .build()).build();
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

        return rates;
    }

    NbpRecord findPreviousNbpDateRate(LocalDate test) {
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

    private static void prepopulateEmptyRates(LinkedHashMap<LocalDate, NbpRecord> rates, int year) {
        LocalDate localDate = LocalDate.of(year, Month.JANUARY, 1);
        LocalDate endOfYear = LocalDate.of(year, Month.DECEMBER, 31);
        while (!localDate.equals(endOfYear)) {
            rates.put(localDate, NbpRecord.EMPTY);
            localDate = localDate.plusDays(1);
        }
    }
}
