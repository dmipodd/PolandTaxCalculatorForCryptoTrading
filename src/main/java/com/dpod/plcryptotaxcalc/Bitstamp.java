package com.dpod.plcryptotaxcalc;

import com.dpod.plcryptotaxcalc.csv.BitstampCsvIndexes;
import com.dpod.plcryptotaxcalc.nbp.NbpDailyRates;
import com.dpod.plcryptotaxcalc.nbp.NbpRates;
import com.dpod.plcryptotaxcalc.report.Currency;
import com.dpod.plcryptotaxcalc.report.Posting;
import com.dpod.plcryptotaxcalc.report.PostingType;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.dpod.plcryptotaxcalc.Utils.createCsvReader;

public class Bitstamp {

    static List<Posting> generatePostingsFor(NbpRates nbpRates, String filename, int year) throws CsvValidationException, IOException {
        try (CSVReader csvReader = createCsvReader(filename, ',')) {
            String[] headers = csvReader.readNext();
            var bitstampCsvIndexes = new BitstampCsvIndexes(headers);
            return calcTax(nbpRates, csvReader, bitstampCsvIndexes);
        }
    }

    private static List<Posting> calcTax(NbpRates nbpRates, CSVReader csvReader, BitstampCsvIndexes bitstampCsvIndexes) throws IOException, CsvValidationException {
        String[] values;
        List<Posting> postings = new ArrayList<>();
        while ((values = csvReader.readNext()) != null) {

            // 2024-01-02T11:52:53Z
            String dateTime = values[bitstampCsvIndexes.dateTime()];
            ZonedDateTime utcZonedDateTime = ZonedDateTime.parse(dateTime);
            ZonedDateTime warsawZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.of("Europe/Warsaw"));
            LocalDate tradeDate = warsawZonedDateTime.toLocalDate();

            Currency currency = Currency.valueOf(values[bitstampCsvIndexes.currency()]);
            NbpDailyRates nbpDailyRates = nbpRates.findRateForPreviousBusinessDay(tradeDate);

            PostingType type = PostingType.fromText(values[bitstampCsvIndexes.action()]);
            Posting tradePosting = Posting.builder()
                    .amount(new BigDecimal(values[bitstampCsvIndexes.amount()]))
                    .currency(currency)
                    .rateDate(nbpDailyRates.getDate())
                    .date(tradeDate)
                    .type(type)
                    .rate(nbpDailyRates.getRateFor(currency))
                    .build();

            Currency feeCurrency = Currency.valueOf(values[bitstampCsvIndexes.feeCurrency()]);
            Posting feePosting = Posting.builder()
                    .amount(new BigDecimal(values[bitstampCsvIndexes.fee()]))
                    .currency(feeCurrency)
                    .rateDate(nbpDailyRates.getDate())
                    .date(tradeDate)
                    .type(PostingType.FEE)
                    .rate(nbpDailyRates.getRateFor(currency))
                    .build();
            postings.add(tradePosting);
            postings.add(feePosting);
        }
        return postings;
    }
}
