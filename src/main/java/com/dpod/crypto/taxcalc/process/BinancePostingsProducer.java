package com.dpod.crypto.taxcalc.process;

import com.dpod.crypto.taxcalc.csv.BinanceCsvIndexes;
import com.dpod.crypto.taxcalc.nbp.NbpDailyRates;
import com.dpod.crypto.taxcalc.nbp.NbpRates;
import com.dpod.crypto.taxcalc.posting.CurrencyAmount;
import com.dpod.crypto.taxcalc.posting.FiatCurrencyAmount;
import com.dpod.crypto.taxcalc.posting.Posting;
import com.dpod.crypto.taxcalc.posting.PostingType;

import java.time.LocalDate;
import java.util.List;

public class BinancePostingsProducer implements PostingsProducer {

    public static final int DATE_END_INDEX_EXCLUSIVE = "yyyy-MM-dd".length();

    @Override
    public List<Posting> createPostingsFor(NbpRates nbpRates, String filename) {
        return createPostingsFor(nbpRates,
                filename,
                BinanceCsvIndexes::new,
                this::populateTwoPostingsFromTransaction);
    }

    /**
     * We are trying to guess whether it is a SELL or BUY depending on receiveAmount or spendAmount columns values in CSV.
     */
    private List<Posting> populateTwoPostingsFromTransaction(String[] row, NbpRates nbpRates, BinanceCsvIndexes indexes) {
        var tradeDate = getTradeDate(row, indexes);
        var nbpDailyRates = nbpRates.findRateForClosestBusinessDayPriorTo(tradeDate);

        CurrencyAmount receivedCurrAmount = CurrencyAmount.parseSpaceDelimited(row[indexes.receiveAmount()]);
        CurrencyAmount spentCurrAmount = CurrencyAmount.parseSpaceDelimited(row[indexes.spendAmount()]);
        boolean receivedFiat = receivedCurrAmount instanceof FiatCurrencyAmount;

        var postingType = receivedFiat ? PostingType.SELL : PostingType.BUY;
        var fiatCurrencyAmount = (FiatCurrencyAmount) (receivedFiat ? receivedCurrAmount : spentCurrAmount);
        Posting tradePosting = createposting(fiatCurrencyAmount, nbpDailyRates, tradeDate, postingType);

        var feeCurrencyAmount = (FiatCurrencyAmount) CurrencyAmount.parseSpaceDelimited(row[indexes.fee()]);
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