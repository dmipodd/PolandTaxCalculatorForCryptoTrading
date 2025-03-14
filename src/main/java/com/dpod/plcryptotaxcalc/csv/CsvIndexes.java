package com.dpod.plcryptotaxcalc.csv;

import java.util.stream.IntStream;

public interface CsvIndexes {

    default int findCsvIndex(String[] headerRow, String columnName) {
        return IntStream.rangeClosed(0, headerRow.length)
                .filter(i -> columnName.equals(headerRow[i]))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("CSV file doesn't contain column " + columnName));
    }
}
