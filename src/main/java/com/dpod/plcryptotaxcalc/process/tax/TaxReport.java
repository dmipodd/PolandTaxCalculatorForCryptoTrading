package com.dpod.plcryptotaxcalc.process.tax;

import com.dpod.plcryptotaxcalc.process.posting.Posting;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public record TaxReport(
        List<Posting> postings,
        BigDecimal taxBase,
        BigDecimal tax) {

    public List<String> toCsvRows() {
        List<String> rows = new ArrayList<>();
        rows.add(Posting.csvHeader());
        postings.stream()
                .map(Posting::toCsvRow)
                .forEach(rows::add);
        rows.addAll(taxSummaryCsvRows());
        return rows;
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