package com.dpod.crypto.taxcalc.posting;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode
@Getter
@ToString
public class Posting {

    private final LocalDate date;
    private final PostingType type;
    private final BigDecimal amount;
    private final FiatCurrency currency;
    private final BigDecimal rate;
    private final LocalDate rateDate;
    private final BigDecimal amountPln;

    @Builder
    public Posting(LocalDate date, PostingType type, BigDecimal amount, FiatCurrency currency, BigDecimal rate, LocalDate rateDate) {
        this.date = date;
        this.type = type;
        this.currency = currency;
        this.rate = rate;
        this.rateDate = rateDate;
        this.amount = negateAmountDependingOnType(type, amount);
        this.amountPln = rate.multiply(this.amount);
    }

    private BigDecimal negateAmountDependingOnType(PostingType type, BigDecimal amount) {
        if (type == PostingType.BUY || type == PostingType.FEE) {
            amount = amount.negate();
        }
        return amount;
    }

    public static String csvHeader() {
        return "date,type,amount,currency,amount in PLN,NBP rate,NBP rate date";
    }

    public String toCsvRow() {
        return String.format("%s,%s,%s,%s,%s,%s,%s", date, type, amount, currency, amountPln, rate, rateDate);
    }
}
