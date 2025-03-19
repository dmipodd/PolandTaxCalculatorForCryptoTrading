package com.dpod.crypto.taxcalc.posting;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyAmountTest {

    @ParameterizedTest
    @CsvSource({
            "100.50 USD,USD,100.50",
            "-9999.99 EUR,EUR,-9999.99",
            "       0.1 USD ,USD,0.1"
    })
    void shouldParseFiatCurrencyAmount(String input, FiatCurrency expectedCurrency, BigDecimal expected) {
        CurrencyAmount result = CurrencyAmount.parseSpaceDelimited(input);

        assertInstanceOf(FiatCurrencyAmount.class, result);
        FiatCurrencyAmount fiat = (FiatCurrencyAmount) result;
        assertEquals(expectedCurrency, fiat.currency());
        assertEquals(expected, fiat.amount());
    }

    @ParameterizedTest
    @CsvSource({
            "0.05 BTC",
            "0.0000001 ETH",
            "    0.0000001 SOL   "
    })
    void shouldParseCryptoCurrencyAmount(String input) {
        CurrencyAmount result = CurrencyAmount.parseSpaceDelimited(input);
        assertInstanceOf(CryptoCurrencyAmount.class, result);
        assertNotNull(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "USD",
            "100.50",
            "0.05 BTC 123",
    })
    @NullAndEmptySource
    void parseSpaceDelimited_NullInput_ShouldThrowException(String str) {
        assertThrows(IllegalArgumentException.class, () -> CurrencyAmount.parseSpaceDelimited(str));
    }
}