package com.dpod.plcryptotaxcalc.csv;

import lombok.Getter;

@Getter
public class NbpRatesCsvIndexes implements CsvIndexes {

    private final int date;
    private final int eur;
    private final int usd;

    public NbpRatesCsvIndexes(String[] headerRow) {
        date = findIndexByName("data", headerRow);
        eur = findIndexByName("1EUR", headerRow);
        usd = findIndexByName("1USD", headerRow);
    }
}
