package com.dpod.crypto.taxcalc.config;

import com.dpod.crypto.taxcalc.process.BinanceTransactionProcessor;
import com.dpod.crypto.taxcalc.process.BitstampTransactionProcessor;
import com.dpod.crypto.taxcalc.process.Processor;

public record AppConfig(
        int year,
        Source source,
        String nbpRatesFileYearBefore,
        String nbpRatesFile,
        String transactionsFile) {

    public Processor getProcessor() {
        return switch (source) {
            case BINANCE -> new BinanceTransactionProcessor();
            case BITSTAMP -> new BitstampTransactionProcessor();
        };
    }
}