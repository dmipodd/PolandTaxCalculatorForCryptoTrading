package com.dpod.plcryptotaxcalc;

import com.dpod.plcryptotaxcalc.csv.BitstampCsvIndexes;
import com.dpod.plcryptotaxcalc.nbp.NbpDailyRates;
import com.dpod.plcryptotaxcalc.nbp.NbpRates;
import com.dpod.plcryptotaxcalc.report.Currency;
import com.dpod.plcryptotaxcalc.report.Posting;
import com.dpod.plcryptotaxcalc.report.PostingType;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.dpod.plcryptotaxcalc.Utils.createCsvReader;

public class Bitstamp {

    public static final BigDecimal TAX_RATE = new BigDecimal("0.19");

    static void bitstamp(NbpRates nbpRates, String filename, int year) throws CsvValidationException, IOException {
        try (CSVReader csvReader = createCsvReader(filename, ',')) {
            String[] headers = csvReader.readNext();
            var bitstampCsvIndexes = new BitstampCsvIndexes(headers);
            calcTax(nbpRates, csvReader, bitstampCsvIndexes, year);
        }
    }

    private static void calcTax(NbpRates nbpRates, CSVReader csvReader, BitstampCsvIndexes bitstampCsvIndexes, int year) throws IOException, CsvValidationException {
        String[] values;
        List<Posting> postings = new ArrayList<>();
        while ((values = csvReader.readNext()) != null) {

            // 2024-01-02T11:52:53Z
            String dateTime = values[bitstampCsvIndexes.dateTime()];
            ZonedDateTime utcZonedDateTime = ZonedDateTime.parse(dateTime);
            ZonedDateTime warsawZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.of("Europe/Warsaw"));
            LocalDate tradeDate = warsawZonedDateTime.toLocalDate();

            Currency currency = Currency.valueOf(values[bitstampCsvIndexes.currency()]);
            NbpDailyRates nbpDailyRates = nbpRates.findRateForPreviousBusinessDay(tradeDate);

            PostingType type = PostingType.fromText(values[bitstampCsvIndexes.action()]);
            Posting tradePosting = Posting.builder()
                    .amount(new BigDecimal(values[bitstampCsvIndexes.amount()]))
                    .currency(currency)
                    .rateDate(nbpDailyRates.getDate())
                    .date(tradeDate)
                    .type(type)
                    .rate(nbpDailyRates.getRateFor(currency))
                    .build();

            Currency feeCurrency = Currency.valueOf(values[bitstampCsvIndexes.feeCurrency()]);
            Posting feePosting = Posting.builder()
                    .amount(new BigDecimal(values[bitstampCsvIndexes.fee()]))
                    .currency(feeCurrency)
                    .rateDate(nbpDailyRates.getDate())
                    .date(tradeDate)
                    .type(PostingType.FEE)
                    .rate(nbpDailyRates.getRateFor(currency))
                    .build();
            postings.add(tradePosting);
            postings.add(feePosting);
        }

        BigDecimal taxBase = postings.stream()
                .map(Posting::getAmountPln)
                .reduce(BigDecimal::add)
                .orElseThrow();
        taxBase = taxBase.setScale(2, RoundingMode.HALF_UP);

        List<String> csvLines = new ArrayList<>();
        csvLines.add(Posting.csvHeader());
        List<String> postingCsvLines = postings.stream().map(Posting::toCsvRow).toList();
        csvLines.addAll(postingCsvLines);
        csvLines.addAll(getSummaryLines(taxBase));

        Path outputFilePath = Path.of("bitstamp.result." + year + "." + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".csv");
        Files.write(outputFilePath, csvLines, StandardOpenOption.CREATE_NEW);
    }

    private static List<String> getSummaryLines(BigDecimal taxBase) {
        List<String> lines = new ArrayList<>();

        // add an empty line as a separator of summary from postings
        lines.add(StringUtils.EMPTY);

        if (taxBase.compareTo(BigDecimal.ZERO) > 0) {
            lines.add("PROFIT IS," + taxBase);
            BigDecimal tax = taxBase
                    .multiply(TAX_RATE)
                    .setScale(2, RoundingMode.HALF_UP);
            lines.add("TAX IS," + tax);
        } else {
            lines.add("LOSS," + taxBase);
        }
        return lines;
    }
}
