package com.dpod.crypto.taxcalc.process;

import com.dpod.crypto.taxcalc.csv.BinanceCsvIndexes;
import com.dpod.crypto.taxcalc.csv.CsvUtils;
import com.dpod.crypto.taxcalc.exception.NbpRatesLoadingException;
import com.dpod.crypto.taxcalc.nbp.NbpDailyRates;
import com.dpod.crypto.taxcalc.nbp.NbpRates;
import com.dpod.crypto.taxcalc.posting.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BinancePostingsProducer implements PostingsProducer {

    public static final int DATE_END_INDEX_EXCLUSIVE = "yyyy-MM-dd".length();

    @Override
    public List<Posting> createPostingsFor(NbpRates nbpRates, String filename) {
        try (var csvReader = CsvUtils.createCsvReader(filename, ',')) {
            String[] headers = csvReader.readNext();
            var csvIndexes = new BinanceCsvIndexes(headers);
            return populatePostingsFrom(nbpRates, csvReader, csvIndexes);
        } catch (CsvValidationException | IOException exception) {
            throw new NbpRatesLoadingException(exception);
        }
    }

    private List<Posting> populatePostingsFrom(NbpRates nbpRates,
                                               CSVReader csvReader,
                                               BinanceCsvIndexes indexes) throws IOException, CsvValidationException {
        List<Posting> postings = new ArrayList<>();
        String[] row;
        while ((row = csvReader.readNext()) != null) {
            List<Posting> twoPostings = populateTwoPostingsFromTransaction(row, nbpRates, indexes);
            postings.addAll(twoPostings);
        }
        return postings;
    }

    private List<Posting> populateTwoPostingsFromTransaction(String[] row, NbpRates nbpRates, BinanceCsvIndexes indexes) {
        var tradeDate = getTradeDate(row, indexes);
        var nbpDailyRates = nbpRates.findRateForClosestBusinessDayPriorTo(tradeDate);

        PostingType postingType;
        FiatCurrencyAmount fiatCurrencyAmount;
        CurrencyAmount receivedCurrencyAmount = CurrencyAmount.parseSpaceDelimited(row[indexes.receiveAmount()]);
        CurrencyAmount spentCurrencyAmount = CurrencyAmount.parseSpaceDelimited(row[indexes.spendAmount()]);
        if (receivedCurrencyAmount instanceof FiatCurrencyAmount) {
            postingType = PostingType.SELL;
            fiatCurrencyAmount = (FiatCurrencyAmount) receivedCurrencyAmount;
        } else {
            postingType = PostingType.BUY;
            fiatCurrencyAmount = (FiatCurrencyAmount) spentCurrencyAmount;
        }
        Posting tradePosting = createposting(fiatCurrencyAmount, nbpDailyRates, tradeDate, postingType);

        var feeCurrencyAmount = (FiatCurrencyAmount) CurrencyAmount.parseSpaceDelimited(row[indexes.spendAmount()]);
        Posting feePosting = createposting(feeCurrencyAmount, nbpDailyRates, tradeDate, PostingType.FEE);

        return List.of(tradePosting, feePosting);
    }

    private Posting createposting(FiatCurrencyAmount currencyAmount, NbpDailyRates nbpDailyRates, LocalDate tradeDate, PostingType postingType) {
        return Posting.builder()
                .amount(currencyAmount.amount())
                .currency(currencyAmount.currency())
                .rateDate(nbpDailyRates.getDate())
                .date(tradeDate)
                .type(postingType)
                .rate(nbpDailyRates.getRateFor(currencyAmount.currency()))
                .build();
    }

    /**
     * Datetime is already in a correct local timezone.
     */
    private LocalDate getTradeDate(String[] row, BinanceCsvIndexes indexes) {
        String dateTimeAsString = row[indexes.dateTime()];
        return LocalDate.parse(dateTimeAsString.substring(0, DATE_END_INDEX_EXCLUSIVE));
    }
}