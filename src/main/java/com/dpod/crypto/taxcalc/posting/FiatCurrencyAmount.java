package com.dpod.crypto.taxcalc.posting;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

public record FiatCurrencyAmount(
        FiatCurrency currency,
        BigDecimal amount) {

    /**
     * @throws NullPointerException if not fiat currency is in #str
     */
    public static FiatCurrencyAmount parseSpaceDelimitedRequired(String str) {
        var currencyAmount = parseSpaceDelimited(str);
        Objects.requireNonNull(currencyAmount, "Expected one of fiat currencies " +
                Arrays.toString(FiatCurrency.values()) + " in a value " + str);
        return currencyAmount;
    }

    /**
     * @return null if not fiat currency is in #str
     */
    public static FiatCurrencyAmount parseSpaceDelimited(String str) {
        str = StringUtils.trimToEmpty(str);
        String[] parts = str.split(" ");
        if (ArrayUtils.getLength(parts) != 2) {
            throw new IllegalArgumentException("Invalid format: Expected '{currency} {amount}', but got: '" + str + "'");
        }

        var currencyString = parts[1];
        if (!FiatCurrency.contains(currencyString)) {
            return null;
        }

        var amount = new BigDecimal(parts[0]);
        var currency = FiatCurrency.valueOf(currencyString);
        return new FiatCurrencyAmount(currency, amount);
    }
}