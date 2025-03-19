package com.dpod.crypto.taxcalc.process;

import com.dpod.crypto.taxcalc.csv.BinanceCsvIndexes;
import com.dpod.crypto.taxcalc.csv.CsvUtils;
import com.dpod.crypto.taxcalc.exception.NbpRatesLoadingException;
import com.dpod.crypto.taxcalc.nbp.NbpDailyRates;
import com.dpod.crypto.taxcalc.nbp.NbpRates;
import com.dpod.crypto.taxcalc.posting.Posting;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BinanceTransactionPostingsProducer implements PostingsProducer {

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
        LocalDate tradeDate = getTradeDate(row, indexes);
        NbpDailyRates nbpDailyRates = nbpRates.findRateForClosestBusinessDayPriorTo(tradeDate);

        // todo finish the implementation and add tests
//        Currency currency = Currency.valueOf(row[indexes.currency()]);
//
//        PostingType type = PostingType.fromText(row[indexes.action()]);
//        Posting tradePosting = Posting.builder()
//                .amount(new BigDecimal(row[indexes.amount()]))
//                .currency(currency)
//                .rateDate(nbpDailyRates.getDate())
//                .date(tradeDate)
//                .type(type)
//                .rate(nbpDailyRates.getRateFor(currency))
//                .build();
//        postings.add(tradePosting);
//
//        Currency feeCurrency = Currency.valueOf(row[indexes.feeCurrency()]);
//        Posting feePosting = Posting.builder()
//                .amount(new BigDecimal(row[indexes.fee()]))
//                .currency(feeCurrency)
//                .rateDate(nbpDailyRates.getDate())
//                .date(tradeDate)
//                .type(PostingType.FEE)
//                .rate(nbpDailyRates.getRateFor(currency))
//                .build();
//        postings.add(feePosting);
        return List.of();
    }

    private LocalDate getTradeDate(String[] row, BinanceCsvIndexes indexes) {
        String dateTimeAsString = row[indexes.dateTime()];
        return LocalDate.parse(dateTimeAsString.substring(0, DATE_END_INDEX_EXCLUSIVE));
    }
}