package com.dpod.crypto.taxcalc.process;

import com.dpod.crypto.taxcalc.csv.CsvUtils;
import com.dpod.crypto.taxcalc.exception.NbpRatesLoadingException;
import com.dpod.crypto.taxcalc.nbp.NbpRates;
import com.dpod.crypto.taxcalc.posting.Posting;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.lang3.function.TriFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface PostingsProducer {

    List<Posting> createPostingsFor(NbpRates nbpRates, String filename);

    default <CSVINDEXES> List<Posting> createPostingsFor(NbpRates nbpRates,
                                                         String filename,
                                                         Function<String[], CSVINDEXES> indexesConstructor,
                                                         TriFunction<String[], NbpRates, CSVINDEXES, List<Posting>> postingsFromLinePopulateFunction) {
        try (var csvReader = CsvUtils.createCsvReader(filename, ',')) {
            String[] headers = csvReader.readNext();
            CSVINDEXES indexes = indexesConstructor.apply(headers);

            List<Posting> postings = new ArrayList<>();
            String[] row;
            while ((row = csvReader.readNext()) != null) {
                List<Posting> postingsFromLine = postingsFromLinePopulateFunction.apply(row, nbpRates, indexes);
                postings.addAll(postingsFromLine);
            }

            return postings;
        } catch (CsvValidationException | IOException exception) {
            throw new NbpRatesLoadingException(exception);
        }
    }
}