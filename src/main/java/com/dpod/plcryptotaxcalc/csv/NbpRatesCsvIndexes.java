package com.dpod.plcryptotaxcalc.csv;

import lombok.Data;

import java.util.function.BiConsumer;

@Data
public class NbpRatesCsvIndexes implements CsvIndexes {

    int date;
    int eur;
    int usd;

    public NbpRatesCsvIndexes(String[] headerRow) {
        setIndexFor("data", NbpRatesCsvIndexes::setDate, headerRow);
        setIndexFor("1EUR", NbpRatesCsvIndexes::setEur, headerRow);
        setIndexFor("1USD", NbpRatesCsvIndexes::setUsd, headerRow);
    }

    private void setIndexFor(String columnName, BiConsumer<NbpRatesCsvIndexes, Integer> setter, String[] headers) {
        int index = findCsvIndex(headers, columnName);
        setter.accept(this, index);
    }
}
