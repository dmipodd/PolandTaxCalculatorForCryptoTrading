package com.dpod.crypto.taxcalc.config;

public record AppConfig(
        int year,
        String nbpRatesFileYearBefore,
        String nbpRatesFile,
        String transactionsFile) {
}