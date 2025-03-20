package com.dpod.crypto.taxcalc.csv;

import static com.dpod.crypto.taxcalc.csv.CsvUtils.findIndexByName;

public record BinanceCsvIndexes(int dateTime,
                                int spendAmount,
                                int receiveAmount,
                                int fee) {

    public BinanceCsvIndexes(String[] headerLine) {
        this(
                findIndexByName("Date(UTC+1)", headerLine),
                findIndexByName("Spend Amount", headerLine),
                findIndexByName("Receive Amount", headerLine),
                findIndexByName("Fee", headerLine)
        );
    }
}