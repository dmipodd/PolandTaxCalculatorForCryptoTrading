package com.dpod.crypto.taxcalc.process.tax;

import com.dpod.crypto.taxcalc.posting.Currency;
import com.dpod.crypto.taxcalc.posting.Posting;
import com.dpod.crypto.taxcalc.posting.PostingType;
import com.dpod.crypto.taxcalc.tax.TaxReport;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaxReportTest {

    @Test
    void testToCsvRowsWithProfit() {
        // given
        Posting posting1 = Posting.builder()
                .date(LocalDate.of(2024, 3, 17))
                .type(PostingType.SELL)
                .amount(new BigDecimal("100.00"))
                .currency(Currency.USD)
                .rate(new BigDecimal("4.50"))
                .rateDate(LocalDate.of(2024, 3, 16))
                .build();
        Posting posting2 = Posting.builder()
                .date(LocalDate.of(2024, 3, 18))
                .type(PostingType.SELL)
                .amount(new BigDecimal("200.00"))
                .currency(Currency.EUR)
                .rate(new BigDecimal("4.75"))
                .rateDate(LocalDate.of(2024, 3, 17))
                .build();

        // when
        TaxReport report = new TaxReport(List.of(posting1, posting2), new BigDecimal("500.00"), new BigDecimal("100.00"));
        List<String> csvRows = report.toCsvRows();

        // then
        assertEquals("date,type,amount,currency,amount in PLN,NBP rate,NBP rate date", csvRows.get(0));
        assertEquals(posting1.toCsvRow(), csvRows.get(1));
        assertEquals(posting2.toCsvRow(), csvRows.get(2));
        assertEquals("", csvRows.get(3));
        assertEquals("PROFIT IS,500.00", csvRows.get(4));
        assertEquals("TAX IS,100.00", csvRows.get(5));
    }
}