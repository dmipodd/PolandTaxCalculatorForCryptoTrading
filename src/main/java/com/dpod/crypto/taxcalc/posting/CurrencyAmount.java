package com.dpod.crypto.taxcalc.posting;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public interface CurrencyAmount {

    static CurrencyAmount parseSpaceDelimited(String str) {
        str = StringUtils.trimToEmpty(str);
        String[] parts = str.split(" ");
        if (ArrayUtils.getLength(parts) != 2) {
            throw new IllegalArgumentException("Invalid format: Expected '{currency} {amount}', but got: '" + str + "'");
        }

        var currencyString = parts[1];
        if (!FiatCurrency.contains(currencyString)) {
            return new CryptoCurrencyAmount();
        }

        var amount = new BigDecimal(parts[0]);
        var currency = FiatCurrency.valueOf(currencyString);
        return new FiatCurrencyAmount(currency, amount);
    }
}