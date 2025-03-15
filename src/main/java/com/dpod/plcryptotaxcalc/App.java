package com.dpod.plcryptotaxcalc;

import java.time.LocalDate;

public class App {
    public static void main(String[] args) throws Exception {
        // take nbp quotes from https://nbp.pl/statystyka-i-sprawozdawczosc/kursy/archiwum-tabela-a-csv-xls/
        String ratesFileYearBefore = "archiwum_tab_a_2023.csv";
        String ratesFile = "archiwum_tab_a_2024.csv";
        String transactionsFile = "TransactionsExport.csv";
        int year = 2024;

        NbpRates nbpRates = new NbpRates(ratesFileYearBefore, ratesFile, 2024);
        Bitstamp.bitstamp(nbpRates, transactionsFile);
//        LinkedHashMap<LocalDate, NbpRecord> rates = readNbpRates("nbp_quotes_2021.csv");
//        testRates(rates);
//        revolut(rates);
//        revolut2022(rates);
//        binance(rates);
//        bitstamp(rates, "bitstamp.csv");
    }

    private static void testRates(NbpRates nbpRates) {
        //        20210304;3,7851;4,554
//        20210305;3,8393;4,5793
//        20210308;3,8665;4,5903
//        20210309;3,8507;4,5844
//        20210310;3,842;4,5718
//        20210311;3,8287;4,5805
//        20210312;3,8521;4,5909
//        20210315;3,8429;4,5836
        printUsdRate(nbpRates, LocalDate.of(2021, 3, 5));
        printUsdRate(nbpRates, LocalDate.of(2021, 3, 6));
        printUsdRate(nbpRates, LocalDate.of(2021, 3, 7));
        printUsdRate(nbpRates, LocalDate.of(2021, 3, 8));
        printUsdRate(nbpRates, LocalDate.of(2021, 3, 9));
        printUsdRate(nbpRates, LocalDate.of(2021, 3, 10));
        printUsdRate(nbpRates, LocalDate.of(2021, 3, 11));
        printUsdRate(nbpRates, LocalDate.of(2021, 3, 12));
        printUsdRate(nbpRates, LocalDate.of(2021, 3, 13));
        printUsdRate(nbpRates, LocalDate.of(2021, 3, 14));
        printUsdRate(nbpRates, LocalDate.of(2021, 3, 15));
        printUsdRate(nbpRates, LocalDate.of(2021, 3, 16));
    }

    private static void printUsdRate(NbpRates nbpRates, LocalDate localDate) {
        NbpRecord nbpRecord = nbpRates.findPreviousNbpDateRate(localDate);
        System.out.println(localDate + ": " + nbpRecord.usdRate);
    }
}
