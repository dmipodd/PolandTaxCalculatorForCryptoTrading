package com.dpod.crypto.taxcalc.tax;

import com.dpod.crypto.taxcalc.posting.Posting;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Class encapsulates the result of tax calculation, tax report. It includes: <br />
 * - a list of all postings for all crypto trades, <br />
 * - tax base (profit or loss), <br />
 * - tax amount.
 */
public record TaxReport(
        List<Posting> postings,
        BigDecimal taxBase,
        BigDecimal tax) {

    public List<String> toCsvRows() {
        List<String> rows = new ArrayList<>();
        rows.add(Posting.csvHeader());
        rows.addAll(rowsForAllPostings());
        rows.addAll(taxSummaryCsvRows());
        return rows;
    }

    private List<String> rowsForAllPostings() {
        return postings.stream()
                .map(Posting::toCsvRow)
                .toList();
    }

    private List<String> taxSummaryCsvRows() {
        List<String> rows = new ArrayList<>();

        // add an empty line as a separator of summary from postings
        rows.add(StringUtils.EMPTY);

        if (taxBase.compareTo(BigDecimal.ZERO) > 0) {
            rows.add("PROFIT IS," + taxBase);
            rows.add("TAX IS," + tax);
        } else {
            rows.add("LOSS," + taxBase);
        }
        return rows;
    }
}