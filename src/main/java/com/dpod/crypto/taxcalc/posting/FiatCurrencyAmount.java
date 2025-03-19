package com.dpod.crypto.taxcalc.posting;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public record FiatCurrencyAmount(FiatCurrency currency, BigDecimal amount) {

    public static FiatCurrencyAmount parseSpaceDelimited(String str) {
        str = StringUtils.trimToEmpty(str);
        String[] parts = str.split(" ");
        if (ArrayUtils.getLength(parts) != 2) {
            throw new IllegalArgumentException("Invalid format: Expected '{currency} {amount}', but got: '" + str + "'");
        }

        String currencyString = parts[0];
        if (!FiatCurrency.contains(currencyString)) {
            return null;
        }

        var currency = FiatCurrency.valueOf(currencyString);
        var amount = new BigDecimal(parts[1]);
        return new FiatCurrencyAmount(currency, amount);
    }
}