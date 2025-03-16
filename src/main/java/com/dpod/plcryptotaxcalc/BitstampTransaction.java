package com.dpod.plcryptotaxcalc;

import com.dpod.plcryptotaxcalc.report.Currency;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BitstampTransaction {

    private final LocalDate transactionDate;
    private final PostingType type;
    private final BigDecimal amount;
    private final Currency currency;
    private final BigDecimal fee;
    private final Currency feeCurrency;

    enum PostingType {
        BUY, SELL;
    }
}

