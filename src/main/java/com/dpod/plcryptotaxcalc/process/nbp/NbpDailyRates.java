package com.dpod.plcryptotaxcalc.process.nbp;

import com.dpod.plcryptotaxcalc.process.posting.Currency;
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

    public BigDecimal getRateFor(Currency currency) {
        return switch (currency) {
            case EUR -> eurRate;
            case USD -> usdRate;
        };
    }
}
