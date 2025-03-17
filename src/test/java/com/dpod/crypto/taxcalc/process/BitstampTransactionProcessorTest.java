package com.dpod.crypto.taxcalc.process;

import com.dpod.crypto.taxcalc.process.nbp.NbpRates;
import com.dpod.crypto.taxcalc.process.posting.Currency;
import com.dpod.crypto.taxcalc.process.posting.Posting;
import com.dpod.crypto.taxcalc.process.posting.PostingType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.dpod.crypto.taxcalc.process.posting.Currency.EUR;
import static com.dpod.crypto.taxcalc.process.posting.Currency.USD;
import static com.dpod.crypto.taxcalc.process.posting.PostingType.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class BitstampTransactionProcessorTest {

    @Test
    void shouldProducePostingsCorrectly() {
        // given
        var year = 2024;
        var nbpRatesFileYearBefore = "archiwum_tab_a_2023.csv";
        var nbpRatesFile = "archiwum_tab_a_2024.csv";
        var transactionsFile = "TestTransactionsExport.csv";
        NbpRates nbpRates = new NbpRates(nbpRatesFileYearBefore, nbpRatesFile, year);

        // when
        List<Posting> postings = BitstampTransactionProcessor.generatePostingsFor(nbpRates, transactionsFile);

        // then
        assertThat(postings).containsExactly(
                createPosting("2024-01-09", SELL, "100.86", EUR, "4.3548", "2024-01-08"),
                createPosting("2024-01-09", FEE, "0.30259", EUR, "4.3548", "2024-01-08"),
                createPosting("2024-02-02", BUY, "91.98", USD, "4.0047", "2024-02-01"),
                createPosting("2024-02-02", FEE, "0.36793", USD, "4.0047", "2024-02-01"),
                createPosting("2024-12-19", SELL, "21.49", EUR, "4.2628", "2024-12-18"),
                createPosting("2024-12-19", FEE, "0.08597", EUR, "4.2628", "2024-12-18"));
    }

    private Posting createPosting(String date, PostingType postingType, String amount, Currency currency,
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