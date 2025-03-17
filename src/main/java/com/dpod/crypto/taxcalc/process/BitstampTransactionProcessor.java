package com.dpod.crypto.taxcalc.process;

import com.dpod.crypto.taxcalc.csv.BitstampCsvIndexes;
import com.dpod.crypto.taxcalc.csv.CsvUtils;
import com.dpod.crypto.taxcalc.exception.NbpRatesLoadingException;
import com.dpod.crypto.taxcalc.process.nbp.NbpDailyRates;
import com.dpod.crypto.taxcalc.process.nbp.NbpRates;
import com.dpod.crypto.taxcalc.process.posting.Currency;
import com.dpod.crypto.taxcalc.process.posting.Posting;
import com.dpod.crypto.taxcalc.process.posting.PostingType;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class BitstampTransactionProcessor {

    // todo generalize code
    public static List<Posting> generatePostingsFor(NbpRates nbpRates, String filename) {
        try (var csvReader = CsvUtils.createCsvReader(filename, ',')) {
            String[] headers = csvReader.readNext();
            var bitstampCsvIndexes = new BitstampCsvIndexes(headers);
            return populatePostingsFrom(nbpRates, csvReader, bitstampCsvIndexes);
        } catch (CsvValidationException | IOException exception) {
            throw new NbpRatesLoadingException(exception);
        }
    }

    private static List<Posting> populatePostingsFrom(NbpRates nbpRates,
                                                      CSVReader csvReader,
                                                      BitstampCsvIndexes indexes) throws IOException, CsvValidationException {
        List<Posting> postings = new ArrayList<>();
        String[] row;
        while ((row = csvReader.readNext()) != null) {
            handleTransaction(row, nbpRates, indexes, postings);
        }
        return postings;
    }

    private static void handleTransaction(String[] row, NbpRates nbpRates, BitstampCsvIndexes indexes, List<Posting> postings) {
        LocalDate tradeDate = getTradeDate(row, indexes);
        Currency currency = Currency.valueOf(row[indexes.currency()]);
        NbpDailyRates nbpDailyRates = nbpRates.findRateForPreviousBusinessDay(tradeDate);

        PostingType type = PostingType.fromText(row[indexes.action()]);
        Posting tradePosting = Posting.builder()
                .amount(new BigDecimal(row[indexes.amount()]))
                .currency(currency)
                .rateDate(nbpDailyRates.getDate())
                .date(tradeDate)
                .type(type)
                .rate(nbpDailyRates.getRateFor(currency))
                .build();
        postings.add(tradePosting);

        Currency feeCurrency = Currency.valueOf(row[indexes.feeCurrency()]);
        Posting feePosting = Posting.builder()
                .amount(new BigDecimal(row[indexes.fee()]))
                .currency(feeCurrency)
                .rateDate(nbpDailyRates.getDate())
                .date(tradeDate)
                .type(PostingType.FEE)
                .rate(nbpDailyRates.getRateFor(currency))
                .build();
        postings.add(feePosting);
    }

    /**
     * CSV contains dateTime in UTC timezone, so we have to covert it to Poland-timezone.
     */
    private static LocalDate getTradeDate(String[] row, BitstampCsvIndexes indexes) {
        String dateTimeAsString = row[indexes.dateTime()];
        ZonedDateTime utcZonedDateTime = ZonedDateTime.parse(dateTimeAsString);
        ZonedDateTime warsawZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.of("Europe/Warsaw"));
        return warsawZonedDateTime.toLocalDate();
    }
}
