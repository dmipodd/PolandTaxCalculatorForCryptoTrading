package com.dpod.plcryptotaxcalc.csv;

import static com.dpod.plcryptotaxcalc.csv.CsvIndexUtil.findIndexByName;

public record NbpCsvIndexes(int date, int eur, int usd) {

    public NbpCsvIndexes(String[] headerRow) {
        this(
                findIndexByName("data", headerRow),
                findIndexByName("1EUR", headerRow),
                findIndexByName("1USD", headerRow)
        );
    }
}
