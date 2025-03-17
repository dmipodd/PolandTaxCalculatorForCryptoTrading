package com.dpod.crypto.taxcalc.process.posting;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder(builderClassName = "PostingBuilder")
@Getter
public class Posting {

    private final LocalDate date;
    private final PostingType type;
    private final BigDecimal amount;
    private final Currency currency;
    private final BigDecimal amountPln;
    private final BigDecimal rate;
    private final LocalDate rateDate;

    public static String csvHeader() {
        return "date,type,amount,currency,amount in PLN,NBP rate,NBP rate date";
    }

    public String toCsvRow() {
        return String.format("%s,%s,%s,%s,%s,%s,%s", date, type, amount, currency, amountPln, rate, rateDate);
    }

    /**
     * Customization of a Lombok-generated builder to handle amount correctly.
     */
    public static class PostingBuilder {

        public Posting build() {
            if (type == PostingType.BUY || type == PostingType.FEE) {
                amount = amount.multiply(new BigDecimal(-1));
            }
            amountPln = rate.multiply(amount);
            return new Posting(date, type, amount, currency, amountPln, rate, rateDate);
        }
    }
}

