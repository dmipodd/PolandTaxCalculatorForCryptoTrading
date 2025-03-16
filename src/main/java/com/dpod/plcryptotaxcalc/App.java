package com.dpod.plcryptotaxcalc;

import com.dpod.plcryptotaxcalc.nbp.NbpRates;

public class App {
    public static void main(String[] args) throws Exception {
        var ratesFileYearBefore = "archiwum_tab_a_2023.csv";
        var ratesFile = "archiwum_tab_a_2024.csv";
        var transactionsFile = "TransactionsExport.csv";
        var year = 2024;

        NbpRates nbpRates = new NbpRates(ratesFileYearBefore, ratesFile, year);
        Bitstamp.bitstamp(nbpRates, transactionsFile, year);
    }
}
