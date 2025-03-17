package com.dpod.crypto.taxcalc.csv;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvUtilsTest {

    @Test
    void shouldFindIndexByName() {
        String[] headerRow = {"Name", "Age", "City"};

        assertEquals(0, CsvUtils.findIndexByName("Name", headerRow));
        assertEquals(1, CsvUtils.findIndexByName("Age", headerRow));
        assertEquals(2, CsvUtils.findIndexByName("City", headerRow));
        assertThrows(IllegalArgumentException.class, () -> CsvUtils.findIndexByName("Country", headerRow));
    }

    @Test
    void shouldCreateCsvReader() throws IOException, CsvException {
        List<String[]> lines;
        try (CSVReader reader = CsvUtils.createCsvReader("test.csv", ',')) {
            lines = reader.readAll();
        }

        assertEquals(3, lines.size());
        assertArrayEquals(new String[]{"Name", "Age", "City"}, lines.get(0));
        assertArrayEquals(new String[]{"John", "30", "New York"}, lines.get(1));
        assertArrayEquals(new String[]{"Alice", "25", "Los Angeles"}, lines.get(2));
    }

    @Test
    void shouldThrowNpeIfFileDoesntExist() {
        assertThrows(
                NullPointerException.class,
                () -> CsvUtils.createCsvReader("non_existing.csv", ',')
        );
    }
}