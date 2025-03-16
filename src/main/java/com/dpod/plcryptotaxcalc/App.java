package com.dpod.plcryptotaxcalc;

import com.dpod.plcryptotaxcalc.nbp.NbpRates;
import com.dpod.plcryptotaxcalc.report.Posting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class App {

    public static void main(String[] args) throws Exception {
        var year = 2024;
        var nbpRatesFileYearBefore = "archiwum_tab_a_2023.csv";
        var nbpRatesFile = "archiwum_tab_a_2024.csv";
        var transactionsFile = "TransactionsExport.csv";

        NbpRates nbpRates = new NbpRates(nbpRatesFileYearBefore, nbpRatesFile, year);
        List<Posting> postings = BitstampTransactionProcessor.generatePostingsFor(nbpRates, transactionsFile);
        TaxReport taxReport = TaxCalculation.calculate(postings);

        writeTaxReportToCsv(taxReport, generateOutputFileName(year));
    }

    private static void writeTaxReportToCsv(TaxReport taxReport, String outputFilename) throws IOException {
        Path outputFilePath = Path.of(outputFilename);
        Files.write(outputFilePath, taxReport.toCsvRows(), StandardOpenOption.CREATE_NEW);
    }

    private static String generateOutputFileName(int year) {
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return String.format("bitstamp.result.%d.%s.csv", year, currentDateTime);
    }
}
