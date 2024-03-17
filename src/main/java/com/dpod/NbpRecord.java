package com.dpod;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NbpRecord {

    public static final NbpRecord EMPTY = new NbpRecord();
    LocalDate date;
    String usdRate;
    String eurRate;
    boolean isUSD;

    public NbpRecord(String date, String usdRate, String eurRate) {
        this.date = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
        this.usdRate = usdRate;
        this.eurRate = eurRate;
    }

    public NbpRecord() {
    }
}
