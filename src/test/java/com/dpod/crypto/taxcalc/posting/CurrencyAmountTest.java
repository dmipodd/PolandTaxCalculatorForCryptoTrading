package com.dpod.crypto.taxcalc.posting;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CurrencyAmountTest {

    @ParameterizedTest
    @CsvSource({
            "100.50 USD,USD,100.50",
            "-9999.99 EUR,EUR,-9999.99",
            "       0.1 USD ,USD,0.1"
    })
    void shouldParseFiatCurrencyAmount(String input, FiatCurrency expectedCurrency, BigDecimal expected) {
        FiatCurrencyAmount result = FiatCurrencyAmount.parseSpaceDelimitedRequired(input);

        assertEquals(expectedCurrency, result.currency());
        assertEquals(expected, result.amount());
    }

    @ParameterizedTest
    @CsvSource({
            "0.05 BTC",
            "0.0000001 ETH",
            "    0.0000001 SOL   "
    })
    void shouldParseNonFiatCurrencyAmount(String input) {
        assertThat(FiatCurrencyAmount.parseSpaceDelimited(input)).isNull();
        assertThrows(NullPointerException.class, () -> FiatCurrencyAmount.parseSpaceDelimitedRequired(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "USD",
            "100.50",
            "0.05 BTC 123",
    })
    @NullAndEmptySource
    void shouldThrowExceptionIfInputStringHasInvalidFormat(String str) {
        assertThrows(IllegalArgumentException.class, () -> FiatCurrencyAmount.parseSpaceDelimited(str));
    }
}