package com.dpod.plcryptotaxcalc.csv;

import static com.dpod.plcryptotaxcalc.csv.CsvUtils.findIndexByName;

public record BitstampCsvIndexes(int dateTime,
                                 int action,
                                 int amount,
                                 int currency,
                                 int fee,
                                 int feeCurrency) {

    public BitstampCsvIndexes(String[] headerRow) {
        this(
                findIndexByName("Datetime", headerRow),
                findIndexByName("Subtype", headerRow),
                findIndexByName("Value", headerRow),
                findIndexByName("Value currency", headerRow),
                findIndexByName("Fee", headerRow),
                findIndexByName("Fee currency", headerRow)
        );
    }
}