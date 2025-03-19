package com.dpod.crypto.taxcalc.posting;

import java.util.Arrays;

public enum FiatCurrency {
    EUR,
    USD;

    public static boolean contains(String currency) {
        return Arrays.stream(values())
                .map(Enum::toString)
                .anyMatch(s -> s.equals(currency));
    }
}