package com.dpod.crypto.taxcalc.process.tax;

import com.dpod.crypto.taxcalc.posting.Currency;
import com.dpod.crypto.taxcalc.posting.Posting;
import com.dpod.crypto.taxcalc.posting.PostingType;
import com.dpod.crypto.taxcalc.tax.TaxCalculation;
import com.dpod.crypto.taxcalc.tax.TaxReport;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaxCalculationTest {

    @Test
    void shouldCalculateTaxForProfit() {
        // given
        var postings = List.of(
                Posting.builder()
                        .date(LocalDate.of(2024, 3, 17))
                        .type(PostingType.SELL)
                        .amount(new BigDecimal("100.00"))
                        .currency(Currency.USD)
                        .rate(new BigDecimal("4.50"))
                        .rateDate(LocalDate.of(2024, 3, 16))
                        .build(),

                Posting.builder()
                        .date(LocalDate.of(2024, 3, 18))
                        .type(PostingType.SELL)
                        .amount(new BigDecimal("200.00"))
                        .currency(Currency.EUR)
                        .rate(new BigDecimal("4.75"))
                        .rateDate(LocalDate.of(2024, 3, 17))
                        .build());

        // when
        TaxReport report = TaxCalculation.calculate(postings);

        // then
        assertEquals(new BigDecimal("1400.00"), report.taxBase());
        assertEquals(new BigDecimal("266.00"), report.tax());
    }

    @Test
    void shouldCalculateTaxForLoss() {
        // given
        var postings = List.of(
                Posting.builder()
                        .date(LocalDate.of(2024, 3, 17))
                        .type(PostingType.BUY)
                        .amount(new BigDecimal("100.00"))
                        .currency(Currency.EUR)
                        .rate(new BigDecimal("4.75"))
                        .rateDate(LocalDate.of(2024, 3, 16))
                        .build(),
                Posting.builder()
                        .date(LocalDate.of(2024, 3, 18))
                        .type(PostingType.BUY)
                        .amount(new BigDecimal("150.00"))
                        .currency(Currency.USD)
                        .rate(new BigDecimal("4.60"))
                        .rateDate(LocalDate.of(2024, 3, 17))
                        .build(),
                Posting.builder()
                        .date(LocalDate.of(2024, 3, 19))
                        .type(PostingType.FEE)
                        .amount(new BigDecimal("50.00"))
                        .currency(Currency.USD)
                        .rate(new BigDecimal("4.55"))
                        .rateDate(LocalDate.of(2024, 3, 18))
                        .build());

        // when
        TaxReport report = TaxCalculation.calculate(postings);

        // then
        assertEquals(new BigDecimal("-1392.50"), report.taxBase());
        assertEquals(BigDecimal.ZERO, report.tax());
    }
}