package com.dpod.crypto.taxcalc.csv;

import static com.dpod.crypto.taxcalc.csv.CsvUtils.findIndexByName;

public record BinanceCsvIndexes(int dateTime,
                                int spendAmount,
                                int receiveAmount,
                                int fee) {

    public BinanceCsvIndexes(String[] headerRow) {
        this(
                findIndexByName("Date(UTC+1)", headerRow),
                findIndexByName("Spend Amount", headerRow),
                findIndexByName("Receive Amount", headerRow),
                findIndexByName("Fee", headerRow)
        );
    }
}