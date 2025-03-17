package com.dpod.crypto.taxcalc;

import com.dpod.crypto.taxcalc.config.ConfigLoader;
import com.dpod.crypto.taxcalc.process.BitstampTransactionProcessor;
import com.dpod.crypto.taxcalc.process.nbp.NbpRates;
import com.dpod.crypto.taxcalc.process.tax.TaxCalculation;

import java.io.IOException;

import static com.dpod.crypto.taxcalc.util.FileUtils.generateOutputFileName;
import static com.dpod.crypto.taxcalc.util.FileUtils.writeRowsToCsv;

public class App {

    public static void main(String[] args) throws IOException {
        var config = ConfigLoader.loadConfig("config.yaml");
        var nbpRates = new NbpRates(config.nbpRatesFileYearBefore(), config.nbpRatesFile(), config.year());
        var postings = BitstampTransactionProcessor.generatePostingsFor(nbpRates, config.transactionsFile());
        var taxReport = TaxCalculation.calculate(postings);
        writeRowsToCsv(taxReport.toCsvRows(), generateOutputFileName(config.year()));
    }
}
