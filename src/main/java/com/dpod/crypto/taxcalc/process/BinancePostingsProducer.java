package com.dpod.crypto.taxcalc.process;

import com.dpod.crypto.taxcalc.csv.BinanceCsvIndexes;
import com.dpod.crypto.taxcalc.nbp.NbpDailyRates;
import com.dpod.crypto.taxcalc.nbp.NbpRates;
import com.dpod.crypto.taxcalc.posting.FiatCurrencyAmount;
import com.dpod.crypto.taxcalc.posting.Posting;
import com.dpod.crypto.taxcalc.posting.PostingType;

import java.time.LocalDate;
import java.util.List;

public class BinancePostingsProducer implements PostingsProducer {

    public static final int DATE_END_INDEX_EXCLUSIVE = "yyyy-MM-dd".length();

    @Override
    public List<Posting> createPostingsFor(NbpRates nbpRates, String filename) {
        return createPostingsFor(this::populateTwoPostingsFromTransaction, nbpRates,
                filename,
                BinanceCsvIndexes::new
        );
    }

    /**
     * We are trying to guess whether it is a SELL or BUY depending on receiveAmount or spendAmount columns values in CSV.
     */
    private List<Posting> populateTwoPostingsFromTransaction(String[] line, NbpRates nbpRates, BinanceCsvIndexes indexes) {
        var tradeDate = getTradeDate(line, indexes);
        var nbpDailyRates = nbpRates.findRateForClosestBusinessDayPriorTo(tradeDate);
        var tradePosting = createTradePosting(line, indexes, tradeDate, nbpDailyRates);
        var feePosting = createFeePosting(line, indexes, tradeDate, nbpDailyRates);
        return List.of(tradePosting, feePosting);
    }

    private Posting createTradePosting(String[] line, BinanceCsvIndexes indexes, LocalDate tradeDate, NbpDailyRates nbpDailyRates) {
        // let's assume that it is SELL-transaction,
        // meaning receiveAmount column contains a fiat currency
        var postingType = PostingType.SELL;
        var currencyAmount = FiatCurrencyAmount.parseSpaceDelimited(line[indexes.receiveAmount()]);

        if (currencyAmount == null) {
            // assumption is wrong, it is BUY transaction,
            // so spendAmount column must contain a fiat currency
            postingType = PostingType.BUY;
            currencyAmount = FiatCurrencyAmount.parseSpaceDelimitedRequired(line[indexes.spendAmount()]);
        }
        return createPosting(currencyAmount, nbpDailyRates, tradeDate, postingType);
    }

    private Posting createFeePosting(String[] line, BinanceCsvIndexes indexes, LocalDate tradeDate, NbpDailyRates nbpDailyRates) {
        var feeCurrencyAmount = FiatCurrencyAmount.parseSpaceDelimitedRequired(line[indexes.fee()]);
        return createPosting(feeCurrencyAmount, nbpDailyRates, tradeDate, PostingType.FEE);
    }

    private Posting createPosting(FiatCurrencyAmount currencyAmount, NbpDailyRates rates, LocalDate tradeDate, PostingType postingType) {
        return Posting.builder()
                .amount(currencyAmount.amount())
                .currency(currencyAmount.currency())
                .rateDate(rates.getDate())
                .date(tradeDate)
                .type(postingType)
                .rate(rates.getRateFor(currencyAmount.currency()))
                .build();
    }

    /**
     * Datetime is already in a correct local timezone.
     */
    private LocalDate getTradeDate(String[] line, BinanceCsvIndexes indexes) {
        String dateTimeAsString = line[indexes.dateTime()];
        return LocalDate.parse(dateTimeAsString.substring(0, DATE_END_INDEX_EXCLUSIVE));
    }
}