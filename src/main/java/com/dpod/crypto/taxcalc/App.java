package com.dpod.crypto.taxcalc;

import com.dpod.crypto.taxcalc.config.ConfigLoader;
import com.dpod.crypto.taxcalc.nbp.NbpRates;
import com.dpod.crypto.taxcalc.util.FileUtils;

import java.io.IOException;

public class App {

    private static final String CONFIG_FILENAME = "config.yaml";

    public static void main(String[] args) throws IOException {
        var config = ConfigLoader.loadConfig(CONFIG_FILENAME);
        var nbpRates = new NbpRates(config.nbpRatesFileFromPreviousYear(), config.nbpRatesFile(), config.year());
        var postings = config.getPostingsProducer().createPostingsFor(nbpRates, config.transactionsFile());
        var taxReport = config.getTaxCalculator().calculate(postings);
        var outputFilename = FileUtils.generateOutputFileName(config);
        FileUtils.writeRowsToCsv(taxReport.toCsvRows(), outputFilename);
    }
}
