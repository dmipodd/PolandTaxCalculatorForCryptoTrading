package com.dpod.plcryptotaxcalc.process.posting;

import java.util.Arrays;

public enum PostingType {

    BUY("Buy"),
    SELL("Sell"),
    FEE("Fee");

    private final String text;

    PostingType(String text) {
        this.text = text;
    }

    public static PostingType fromText(String text) {
        return Arrays.stream(values())
                .filter(postingType -> text.equals(postingType.text))
                .findFirst()
                .orElseThrow();
    }
}
