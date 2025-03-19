package com.dpod.crypto.taxcalc.nbp;

import com.dpod.crypto.taxcalc.posting.FiatCurrency;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class NbpDailyRates {

    private final LocalDate date;
    private final BigDecimal usdRate;
    private final BigDecimal eurRate;

    public NbpDailyRates(LocalDate date, String usdRate, String eurRate) {
        this.date = date;
        this.usdRate = convertFormatFor(usdRate);
        this.eurRate = convertFormatFor(eurRate);
    }

    private BigDecimal convertFormatFor(String rate) {
        rate = rate.replace(',', '.');
        return new BigDecimal(rate);
    }

    public BigDecimal getRateFor(FiatCurrency currency) {
        return switch (currency) {
            case EUR -> eurRate;
            case USD -> usdRate;
        };
    }
}
