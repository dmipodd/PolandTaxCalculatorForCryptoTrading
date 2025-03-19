package com.dpod.crypto.taxcalc.process.tax;

import com.dpod.crypto.taxcalc.posting.FiatCurrency;
import com.dpod.crypto.taxcalc.posting.Posting;
import com.dpod.crypto.taxcalc.posting.PostingType;
import com.dpod.crypto.taxcalc.tax.TaxReport;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class TaxReportTest {

    @Test
    void testToCsvRowsWithProfit() {
        // given
        Posting posting1 = Posting.builder()
                .date(LocalDate.of(2024, 3, 17))
                .type(PostingType.SELL)
                .amount(new BigDecimal("100.00"))
                .currency(FiatCurrency.USD)
                .rate(new BigDecimal("4.50"))
                .rateDate(LocalDate.of(2024, 3, 16))
                .build();
        Posting posting2 = Posting.builder()
                .date(LocalDate.of(2024, 3, 18))
                .type(PostingType.SELL)
                .amount(new BigDecimal("200.00"))
                .currency(FiatCurrency.EUR)
                .rate(new BigDecimal("4.75"))
                .rateDate(LocalDate.of(2024, 3, 17))
                .build();

        // when
        TaxReport report = new TaxReport(
                List.of(posting1, posting2),
                new BigDecimal("754.12"), new BigDecimal("-123.98"),
                new BigDecimal("630.14"), new BigDecimal("119.73"));
        List<String> csvRows = report.toCsvRows();

        // then
        assertThat(csvRows).containsExactly(
                "",
                ",,,POSTINGS,,,",
                "",
                "date,type,amount,currency,amount in PLN,NBP rate,NBP rate date",
                posting1.toCsvRow(),
                posting2.toCsvRow(),
                "",
                "",
                ",,,TAX REPORT,,,",
                "",
                ",,revenue,expenses",
                ",,754.12,-123.98",
                "",
                ",,taxBase(profit),tax",
                ",,630.14,119.73"
        );
    }
}