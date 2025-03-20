package com.dpod.crypto.taxcalc.csv;

public record NbpCsvIndexes(int date, int eur, int usd) {

    public NbpCsvIndexes(String[] headerLine) {
        this(
                CsvUtils.findIndexByName("data", headerLine),
                CsvUtils.findIndexByName("1EUR", headerLine),
                CsvUtils.findIndexByName("1USD", headerLine)
        );
    }
}
