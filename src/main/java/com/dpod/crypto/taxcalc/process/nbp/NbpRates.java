package com.dpod.crypto.taxcalc.process.nbp;

import com.dpod.crypto.taxcalc.csv.CsvUtils;
import com.dpod.crypto.taxcalc.exception.NbpRatesLoadingException;
import com.dpod.crypto.taxcalc.csv.NbpCsvIndexes;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Class encapsulates information about National Polish Bank average daily currency rates.
 * Main purpose of the class is to find the previous business day preceding a trade day.
 * The source of information is a pair of CSV files for a specified year and for previous year.
 * We need only a small fraction of previous year data for January 2 corner case.
 */
public class NbpRates {

    private static final DateTimeFormatter NBP_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int PREVIOUS_YEAR_DECEMBER_DATE_TO_READ_FROM = 15;

    private final LinkedHashMap<LocalDate, NbpDailyRates> rates;
    private final int year;

    public NbpRates(String ratesFileYearBefore, String currencyRatesFile, int year) {
        this.year = year;
        this.rates = readNbpRatesFromFiles(ratesFileYearBefore, currencyRatesFile);
    }

    /**
     * Finds NBP rates from the closest business day preceding specified #date.<br />
     * Examples:
     * <ul>
     *  <li>
     *      you did a trade on March 12, 2025 that was Wednesday. Method will return March 11, 2025 that is Tuesday.
     *  </li>
     *  <li>
     *      you did a trade on March 17, 2025 that was Monday. Method will return March 14, 2025 that is Friday because weekend is skipped.
     *  </li>
     *  <li>
     *      you did a trade on January 2, 2024 that was Tuesday. Method will return December 29, 2023 that is Friday
     *      because weekend is skipped and bank holiday January 1 is skipped as well.
     *  </li>
     * </ul>
     */
    public NbpDailyRates findRateForPreviousBusinessDay(LocalDate date) {
        var previousDay = date;
        NbpDailyRates dailyRates;
        do {
            previousDay = previousDay.minusDays(1);
            dailyRates = rates.get(previousDay);
        } while (dailyRates == null);
        return dailyRates;
    }

    private LinkedHashMap<LocalDate, NbpDailyRates> readNbpRatesFromFiles(String ratesFileYearBefore, String ratesFile) {
        var rates = new LinkedHashMap<LocalDate, NbpDailyRates>();
        loadRatesFromFile(ratesFile, rates, this::notValidDate);
        loadRatesFromFile(ratesFileYearBefore, rates, anyOf(this::notValidDate, this::dateIsBeforeMidDecemberOfPreviousYear));
        return rates;
    }

    private void loadRatesFromFile(String filename, LinkedHashMap<LocalDate, NbpDailyRates> rates, Predicate<String> skipRowPredicate) {
        try (var csvReader = CsvUtils.createCsvReader(filename, ';')) {
            var headerRow = csvReader.readNext();
            var indexes = new NbpCsvIndexes(headerRow);
            loadDailyRatesFromCsvRow(rates, csvReader, indexes, skipRowPredicate);
        } catch (CsvValidationException | IOException exception) {
            throw new NbpRatesLoadingException(exception);
        }
    }

    private void loadDailyRatesFromCsvRow(LinkedHashMap<LocalDate, NbpDailyRates> rates,
                                          CSVReader csvReader,
                                          NbpCsvIndexes indexes,
                                          Predicate<String> skipRowPredicate) throws IOException, CsvValidationException {
        String[] row;
        while ((row = csvReader.readNext()) != null) {
            String dateAsString = row[indexes.date()];

            if (skipRowPredicate.test(dateAsString)) {
                continue;
            }

            var date = LocalDate.parse(dateAsString, NBP_DATE_FORMAT);
            var usdRate = row[indexes.usd()];
            var eurRate = row[indexes.eur()];
            var dailyRates = new NbpDailyRates(date, usdRate, eurRate);
            rates.put(date, dailyRates);
        }
    }

    public static <T> Predicate<T> anyOf(Predicate<T>... predicates) {
        return Stream.of(predicates)
                .reduce(Predicate::or)
                .orElseThrow();
    }

    private boolean dateIsBeforeMidDecemberOfPreviousYear(String dateAsString) {
        var previousYear = String.valueOf(year - 1);
        var december = "12";
        return dateAsString.compareTo(previousYear + december + PREVIOUS_YEAR_DECEMBER_DATE_TO_READ_FROM) < 0;
    }

    private boolean notValidDate(String dateAsString) {
        return !StringUtils.isNumeric(dateAsString);
    }
}