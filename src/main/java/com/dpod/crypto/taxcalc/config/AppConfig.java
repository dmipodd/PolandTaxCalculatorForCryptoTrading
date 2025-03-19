package com.dpod.crypto.taxcalc.config;

import com.dpod.crypto.taxcalc.process.BinancePostingsProducer;
import com.dpod.crypto.taxcalc.process.BitstampPostingsProducer;
import com.dpod.crypto.taxcalc.process.PostingsProducer;
import com.dpod.crypto.taxcalc.tax.TaxCalculator;

public record AppConfig(
        int year,
        Source source,
        String nbpRatesFileFromPreviousYear,
        String nbpRatesFile,
        String transactionsFile) {

    public PostingsProducer getPostingsProducer() {
        return switch (source) {
            case BINANCE -> new BinancePostingsProducer();
            case BITSTAMP -> new BitstampPostingsProducer();
        };
    }

    public TaxCalculator getTaxCalculator() {
        return new TaxCalculator();
    }
}