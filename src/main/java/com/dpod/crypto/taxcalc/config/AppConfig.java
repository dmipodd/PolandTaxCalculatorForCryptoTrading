package com.dpod.crypto.taxcalc.config;

import com.dpod.crypto.taxcalc.process.BinanceTransactionPostingsProducer;
import com.dpod.crypto.taxcalc.process.BitstampTransactionPostingsProducer;
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
            case BINANCE -> new BinanceTransactionPostingsProducer();
            case BITSTAMP -> new BitstampTransactionPostingsProducer();
        };
    }

    public TaxCalculator getTaxCalculator() {
        return new TaxCalculator();
    }
}