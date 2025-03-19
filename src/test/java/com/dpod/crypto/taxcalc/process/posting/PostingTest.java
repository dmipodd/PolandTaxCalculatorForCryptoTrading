package com.dpod.crypto.taxcalc.process.posting;

import com.dpod.crypto.taxcalc.posting.FiatCurrency;
import com.dpod.crypto.taxcalc.posting.Posting;
import com.dpod.crypto.taxcalc.posting.PostingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PostingTest {

    @Test
    void testCsvHeader() {
        assertEquals(
                "date,type,amount,currency,amount in PLN,NBP rate,NBP rate date",
                Posting.csvHeader());
    }

    @ParameterizedTest
    @CsvSource({
            "2024-03-17, SELL, 100.00, USD, 4.50, 2024-03-16, 450.0000",
            "2024-03-17, BUY,  100.00, USD, 4.50, 2024-03-16, -450.0000",
            "2024-03-17, SELL, 200.00, EUR, 4.75, 2024-03-16, 950.0000"
    })
    void testPostingBuilderAndCsvRow(LocalDate date, PostingType postingType, BigDecimal amount, FiatCurrency currency,
                                     BigDecimal rate, LocalDate rateDate, String expectedAmountPln) {
        // given
        Posting posting = Posting.builder()
                .date(date)
                .type(postingType)
                .amount(amount)
                .currency(currency)
                .rate(rate)
                .rateDate(rateDate)
                .build();

        // when, then
        assertEquals(new BigDecimal(expectedAmountPln), posting.getAmountPln());
        assertEquals(String.format("%s,%s,%s,%s,%s,%s,%s",
                date, postingType,
                postingType == PostingType.BUY ? amount.negate() : amount,
                currency, expectedAmountPln, rate, rateDate), posting.toCsvRow());
    }
}