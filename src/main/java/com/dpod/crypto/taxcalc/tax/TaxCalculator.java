package com.dpod.crypto.taxcalc.tax;

import com.dpod.crypto.taxcalc.posting.Posting;
import com.dpod.crypto.taxcalc.util.BigDecimalUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Predicate;

import static com.dpod.crypto.taxcalc.util.BigDecimalUtils.isPositive;

public class TaxCalculator {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.19");

    public TaxReport calculate(List<Posting> postings) {
        var revenue = calculateSumOfAmountPln(postings, BigDecimalUtils::isPositive);
        var expenses = calculateSumOfAmountPln(postings, Predicate.not(BigDecimalUtils::isPositive));
        var taxBase = revenue.add(expenses);
        var tax = calculateTaxFrom(taxBase);
        return new TaxReport(postings, revenue, expenses, taxBase, tax);
    }

    private BigDecimal calculateSumOfAmountPln(List<Posting> postings, Predicate<BigDecimal> amountPlnPredicate) {
        BigDecimal taxBase = postings.stream()
                .map(Posting::getAmountPln)
                .filter(amountPlnPredicate)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
        taxBase = taxBase.setScale(2, RoundingMode.HALF_UP);
        return taxBase;
    }

    private BigDecimal calculateTaxFrom(BigDecimal taxBase) {
        return isPositive(taxBase)
                ? taxBase.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
    }
}