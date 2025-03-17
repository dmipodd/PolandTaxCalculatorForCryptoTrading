package com.dpod.crypto.taxcalc.process.posting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PostingTypeTest {

    @ParameterizedTest
    @CsvSource({
            "Buy, BUY",
            "Sell, SELL",
            "Fee, FEE"
    })
    void shouldGetFromText(String input, PostingType expected) {
        assertEquals(expected, PostingType.fromText(input));
    }

    @Test
    void shouldThrowExceptionUponGetFromInvalidText() {
        assertThrows(RuntimeException.class, () -> PostingType.fromText("Invalid"));
    }
}