package com.dpod.crypto.taxcalc.process;

import com.dpod.crypto.taxcalc.nbp.NbpRates;
import com.dpod.crypto.taxcalc.posting.FiatCurrency;
import com.dpod.crypto.taxcalc.posting.Posting;
import com.dpod.crypto.taxcalc.posting.PostingType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.dpod.crypto.taxcalc.posting.FiatCurrency.EUR;
import static com.dpod.crypto.taxcalc.posting.PostingType.BUY;
import static com.dpod.crypto.taxcalc.posting.PostingType.FEE;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class BinancePostingsProducerTest {

    @Test
    void shouldProducePostingsCorrectly() {
        // given
        var year = 2024;
        var nbpRatesFileYearBefore = "archiwum_tab_a_2023.csv";
        var nbpRatesFile = "archiwum_tab_a_2024.csv";
        var transactionsFile = "binance_trade_history.csv";
        var nbpRates = new NbpRates(nbpRatesFileYearBefore, nbpRatesFile, year);
        var processor = new BinancePostingsProducer();

        // when
        List<Posting> postings = processor.createPostingsFor(nbpRates, transactionsFile);

        // then
        assertThat(postings).containsExactly(
                createPosting("2024-03-29", BUY, "20.00", EUR, "4.3191", "2024-03-28"),
                createPosting("2024-03-29", FEE, "0.02", EUR, "4.3191", "2024-03-28"));
    }

    private Posting createPosting(String date, PostingType postingType, String amount, FiatCurrency currency,
                                  String rate, String rateDate) {
        return Posting.builder()
                .date(LocalDate.parse(date))
                .type(postingType)
                .amount(new BigDecimal(amount))
                .currency(currency)
                .rate(new BigDecimal(rate))
                .rateDate(LocalDate.parse(rateDate))
                .build();
    }
}