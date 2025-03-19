package com.dpod.crypto.taxcalc.tax;

import com.dpod.crypto.taxcalc.posting.Posting;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.dpod.crypto.taxcalc.util.BigDecimalUtils.isPositive;

/**
 * Class encapsulates the result of tax calculation, i.e. tax report. <br />
 * It includes:
 * <ul>
 *  <li>a list of all postings for all crypto trades, </li>
 *  <li>revenue and expenses, </li>
 *  <li>tax base (profit or loss), </li>
 *  <li>tax amount</li>
 * </ul>
 */
public record TaxReport(
        List<Posting> postings,
        BigDecimal revenue,
        BigDecimal expenses,
        BigDecimal taxBase,
        BigDecimal tax) {

    public List<String> toCsvRows() {
        List<String> rows = new ArrayList<>();
        rows.add(emptyRow());
        rows.add(centeredTitleRow("POSTINGS"));
        rows.add(emptyRow());
        rows.add(Posting.csvHeader());
        rows.addAll(rowsForAllPostings());
        rows.add(emptyRow());
        rows.add(emptyRow());
        rows.add(centeredTitleRow("TAX REPORT"));
        rows.add(emptyRow());
        rows.addAll(taxSummaryCsvRows());
        return rows;
    }

    private String emptyRow() {
        return StringUtils.EMPTY;
    }

    private String centeredTitleRow(String title) {
        return String.format(",,,%s,,,", title);
    }

    private List<String> rowsForAllPostings() {
        return postings.stream()
                .map(Posting::toCsvRow)
                .toList();
    }

    private List<String> taxSummaryCsvRows() {
        List<String> rows = new ArrayList<>();
        rows.add(",,revenue,expenses");
        rows.add(String.format(",,%s,%s", revenue, expenses));
        rows.add(emptyRow());
        rows.add(String.format(",,taxBase(%s),tax", isPositive(taxBase) ? "profit" : "loss"));
        rows.add(String.format(",,%s,%s", taxBase, tax));
        return rows;
    }
}