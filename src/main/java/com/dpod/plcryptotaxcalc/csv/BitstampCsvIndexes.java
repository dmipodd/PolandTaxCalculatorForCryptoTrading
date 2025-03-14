package com.dpod.plcryptotaxcalc.csv;

import lombok.Getter;

@Getter
public class BitstampCsvIndexes implements CsvIndexes {

    private final int dateTime;
    private final int fee;
    private final int feeCurrency;
    private final int currency;
    private final int value;
    private final int action;

    public BitstampCsvIndexes(String[] headerRow) {
        feeCurrency = findIndexByName("Fee currency", headerRow);
        fee = findIndexByName("Fee", headerRow);
        action = findIndexByName("Subtype", headerRow);
        currency = findIndexByName("Value currency", headerRow);
        value = findIndexByName("Value", headerRow);
        dateTime = findIndexByName("Datetime", headerRow);
    }
}