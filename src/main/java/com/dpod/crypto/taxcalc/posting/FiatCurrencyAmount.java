package com.dpod.crypto.taxcalc.posting;

import java.math.BigDecimal;

public record FiatCurrencyAmount(
        FiatCurrency currency,
        BigDecimal amount) implements CurrencyAmount {
}