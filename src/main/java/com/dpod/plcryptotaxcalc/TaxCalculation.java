package com.dpod.plcryptotaxcalc;

import com.dpod.plcryptotaxcalc.report.Posting;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RequiredArgsConstructor
public class TaxCalculation {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.19");

    public static TaxReport calculate(List<Posting> postings) {
        var taxBase = calculateTaxBase(postings);
        var tax = calculateTaxFrom(taxBase);
        return new TaxReport(postings, taxBase, tax);
    }

    private static BigDecimal calculateTaxBase(List<Posting> postings) {
        BigDecimal taxBase = postings.stream()
                .map(Posting::getAmountPln)
                .reduce(BigDecimal::add)
                .orElseThrow();
        taxBase = taxBase.setScale(2, RoundingMode.HALF_UP);
        return taxBase;
    }

    private static BigDecimal calculateTaxFrom(BigDecimal taxBase) {
        if (taxBase.compareTo(BigDecimal.ZERO) < 0) {
            return new BigDecimal(0);
        } else {
            return taxBase
                    .multiply(TAX_RATE)
                    .setScale(2, RoundingMode.HALF_UP);
        }
    }
}