package com.dpod.plcryptotaxcalc.csv;

import java.util.stream.IntStream;

class CsvIndexUtil {

    static int findIndexByName(String columnName, String[] headerRow) {
        return IntStream.range(0, headerRow.length)
                .filter(i -> columnName.equals(headerRow[i]))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("CSV file doesn't contain column " + columnName));
    }
}
