package com.dpod.crypto.taxcalc.process;

import com.dpod.crypto.taxcalc.csv.BitstampCsvIndexes;
import com.dpod.crypto.taxcalc.nbp.NbpDailyRates;
import com.dpod.crypto.taxcalc.nbp.NbpRates;
import com.dpod.crypto.taxcalc.posting.FiatCurrency;
import com.dpod.crypto.taxcalc.posting.Posting;
import com.dpod.crypto.taxcalc.posting.PostingType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class BitstampPostingsProducer implements PostingsProducer {

    @Override
    public List<Posting> createPostingsFor(NbpRates nbpRates, String filename) {
        return createPostingsFor(this::populateTwoPostingsFromTransaction, nbpRates,
                filename,
                BitstampCsvIndexes::new
        );
    }

    private List<Posting> populateTwoPostingsFromTransaction(String[] line, NbpRates nbpRates, BitstampCsvIndexes indexes) {
        LocalDate tradeDate = getTradeDate(line, indexes);
        FiatCurrency currency = FiatCurrency.valueOf(line[indexes.currency()]);
        NbpDailyRates nbpDailyRates = nbpRates.findRateForClosestBusinessDayPriorTo(tradeDate);

        PostingType type = PostingType.fromText(line[indexes.action()]);
        Posting tradePosting = Posting.builder()
                .amount(new BigDecimal(line[indexes.amount()]))
                .currency(currency)
                .rateDate(nbpDailyRates.getDate())
                .date(tradeDate)
                .type(type)
                .rate(nbpDailyRates.getRateFor(currency))
                .build();

        FiatCurrency feeCurrency = FiatCurrency.valueOf(line[indexes.feeCurrency()]);
        Posting feePosting = Posting.builder()
                .amount(new BigDecimal(line[indexes.fee()]))
                .currency(feeCurrency)
                .rateDate(nbpDailyRates.getDate())
                .date(tradeDate)
                .type(PostingType.FEE)
                .rate(nbpDailyRates.getRateFor(currency))
                .build();
        return List.of(tradePosting, feePosting);
    }

    /**
     * CSV contains dateTime in UTC timezone, so we have to covert it to Poland-timezone.
     */
    private LocalDate getTradeDate(String[] line, BitstampCsvIndexes indexes) {
        String dateTimeAsString = line[indexes.dateTime()];
        ZonedDateTime utcZonedDateTime = ZonedDateTime.parse(dateTimeAsString);
        ZonedDateTime warsawZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.of("Europe/Warsaw"));
        return warsawZonedDateTime.toLocalDate();
    }
}
