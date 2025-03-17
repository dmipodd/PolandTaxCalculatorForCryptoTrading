package com.dpod.crypto.taxcalc.process.nbp;

import com.dpod.crypto.taxcalc.process.posting.Currency;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NbpDailyRatesTest {

    @Test
    void shouldConstructRatesFromCorrectInput() {
        // given
        var date = LocalDate.of(2024, 3, 17);
        var expectedUsdRate = new BigDecimal("4.5678");
        var expectedEurRate = new BigDecimal("5.4321");

        // when
        var rates = new NbpDailyRates(date, "4,5678", "5,4321");

        // then
        assertEquals(date, rates.getDate());
        assertEquals(expectedUsdRate, rates.getUsdRate());
        assertEquals(expectedUsdRate, rates.getRateFor(Currency.USD));
        assertEquals(expectedEurRate, rates.getEurRate());
        assertEquals(expectedEurRate, rates.getRateFor(Currency.EUR));
    }

    @Test
    void shouldThrowExceptionUponInvalidRateFormat() {
        assertThrows(
                NumberFormatException.class,
                () -> new NbpDailyRates(LocalDate.now(), "invalid", "5,4321"));
        assertThrows(
                NumberFormatException.class,
                () -> new NbpDailyRates(LocalDate.now(), "4,5678", "invalid"));
    }
}