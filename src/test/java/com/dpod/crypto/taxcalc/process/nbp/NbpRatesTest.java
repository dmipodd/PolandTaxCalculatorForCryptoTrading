package com.dpod.crypto.taxcalc.process.nbp;

import com.dpod.crypto.taxcalc.nbp.NbpDailyRates;
import com.dpod.crypto.taxcalc.nbp.NbpRates;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NbpRatesTest {

    private static NbpRates nbpRates;

    @BeforeAll
    static void setUp() {
        // given
        var year = 2024;
        var nbpRatesFileYearBefore = "archiwum_tab_a_2023.csv";
        var nbpRatesFile = "archiwum_tab_a_2024.csv";

        nbpRates = new NbpRates(nbpRatesFileYearBefore, nbpRatesFile, year);
    }

    @ParameterizedTest
    @CsvSource({
            "2024-01-01, 2023-12-29, 3.9350, 4.3480",
            "2024-01-02, 2023-12-29, 3.9350, 4.3480",
            "2024-01-03, 2024-01-02, 3.9432, 4.3434",
            "2024-01-04, 2024-01-03, 3.9909, 4.3646",
            "2024-01-05, 2024-01-04, 3.9684, 4.3525",
            "2024-01-06, 2024-01-05, 3.9850, 4.3484",
            "2024-01-07, 2024-01-05, 3.9850, 4.3484",
            "2024-01-08, 2024-01-05, 3.9850, 4.3484",
            "2024-05-01, 2024-04-30, 4.0341, 4.3213",
            "2024-05-02, 2024-04-30, 4.0341, 4.3213",
            "2024-05-03, 2024-05-02, 4.0474, 4.3323",
            "2024-05-04, 2024-05-02, 4.0474, 4.3323",
            "2024-05-05, 2024-05-02, 4.0474, 4.3323",
            "2024-05-06, 2024-05-02, 4.0474, 4.3323",
            "2024-05-07, 2024-05-06, 4.0202, 4.3294",
            "2024-05-07, 2024-05-06, 4.0202, 4.3294",
            "2024-12-24, 2024-12-23, 4.0950, 4.2621",
            "2024-12-25, 2024-12-24, 4.1127, 4.2739",
            "2024-12-26, 2024-12-24, 4.1127, 4.2739",
            "2024-12-27, 2024-12-24, 4.1127, 4.2739",
            "2024-12-28, 2024-12-27, 4.1036, 4.2747",
            "2024-12-29, 2024-12-27, 4.1036, 4.2747",
            "2024-12-30, 2024-12-27, 4.1036, 4.2747",
            "2024-12-31, 2024-12-30, 4.0960, 4.2738"
    })
    public void shouldCorrectlyLoadNbpRatesAndCalculatePreviousBusinessDay(LocalDate localDate,
                                                                           LocalDate expectedDate,
                                                                           BigDecimal expectedUsdRate,
                                                                           BigDecimal expectedEurRate) {
        // when
        NbpDailyRates result = nbpRates.findRateForPreviousBusinessDay(localDate);

        // then
        assertEquals(expectedDate, result.getDate());
        assertEquals(expectedEurRate, result.getEurRate());
        assertEquals(expectedUsdRate, result.getUsdRate());
    }
}