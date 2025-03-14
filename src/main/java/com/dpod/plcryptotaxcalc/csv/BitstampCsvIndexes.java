package com.dpod.plcryptotaxcalc.csv;

import lombok.Data;

import java.util.function.BiConsumer;

@Data
public class BitstampCsvIndexes implements CsvIndexes {

    int dateTime;
    int fee;
    int feeCurrency;
    int currency;
    int value;
    int action;

    public BitstampCsvIndexes(String[] headerRow) {
        setIndex("Fee currency", BitstampCsvIndexes::setFeeCurrency, headerRow);
        setIndex("Fee", BitstampCsvIndexes::setFee, headerRow);
        setIndex("Subtype", BitstampCsvIndexes::setAction, headerRow);
        setIndex("Value currency", BitstampCsvIndexes::setCurrency, headerRow);
        setIndex("Value", BitstampCsvIndexes::setValue, headerRow);
        setIndex("Datetime", BitstampCsvIndexes::setDateTime, headerRow);
    }

    // todo is it possible to move to interface the method?
    private void setIndex(String columnName, BiConsumer<BitstampCsvIndexes, Integer> setter, String[] headers) {
        int index = findCsvIndex(headers, columnName);
        setter.accept(this, index);
    }
}
