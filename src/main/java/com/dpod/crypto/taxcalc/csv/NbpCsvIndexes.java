package com.dpod.crypto.taxcalc.csv;

public record NbpCsvIndexes(int date, int eur, int usd) {

    public NbpCsvIndexes(String[] headerRow) {
        this(
                CsvUtils.findIndexByName("data", headerRow),
                CsvUtils.findIndexByName("1EUR", headerRow),
                CsvUtils.findIndexByName("1USD", headerRow)
        );
    }
}
