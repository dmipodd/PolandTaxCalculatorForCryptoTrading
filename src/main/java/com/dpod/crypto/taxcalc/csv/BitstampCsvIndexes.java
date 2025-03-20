package com.dpod.crypto.taxcalc.csv;

import static com.dpod.crypto.taxcalc.csv.CsvUtils.findIndexByName;

public record BitstampCsvIndexes(int dateTime,
                                 int action,
                                 int amount,
                                 int currency,
                                 int fee,
                                 int feeCurrency) {

    public BitstampCsvIndexes(String[] headerLine) {
        this(
                findIndexByName("Datetime", headerLine),
                findIndexByName("Subtype", headerLine),
                findIndexByName("Value", headerLine),
                findIndexByName("Value currency", headerLine),
                findIndexByName("Fee", headerLine),
                findIndexByName("Fee currency", headerLine)
        );
    }
}