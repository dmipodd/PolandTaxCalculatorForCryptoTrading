package com.dpod.crypto.taxcalc.process;

import com.dpod.crypto.taxcalc.csv.CsvUtils;
import com.dpod.crypto.taxcalc.exception.NbpRatesLoadingException;
import com.dpod.crypto.taxcalc.nbp.NbpRates;
import com.dpod.crypto.taxcalc.posting.Posting;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.lang3.function.TriFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface PostingsProducer {

    List<Posting> createPostingsFor(NbpRates nbpRates, String filename);

    default <CSVINDEXES> List<Posting> createPostingsFor(TriFunction<String[], NbpRates, CSVINDEXES, List<Posting>> lineToPostingsFunc,
                                                         NbpRates nbpRates,
                                                         String filename,
                                                         Function<String[], CSVINDEXES> indexesConstructor) {
        try (var csvReader = CsvUtils.createCsvReader(filename, ',')) {
            String[] headerLine = csvReader.readNext();
            CSVINDEXES indexes = indexesConstructor.apply(headerLine);
            return populatePostingsFromLines(lineToPostingsFunc, nbpRates, csvReader, indexes);
        } catch (CsvValidationException | IOException exception) {
            throw new NbpRatesLoadingException(exception);
        }
    }

    private <CSVINDEXES> List<Posting> populatePostingsFromLines(TriFunction<String[], NbpRates, CSVINDEXES, List<Posting>> lineToPostingsFunc,
                                                                 NbpRates nbpRates,
                                                                 CSVReader csvReader,
                                                                 CSVINDEXES indexes) throws IOException, CsvValidationException {
        List<Posting> postings = new ArrayList<>();
        String[] line;
        while ((line = csvReader.readNext()) != null) {
            List<Posting> postingsFromLine = lineToPostingsFunc.apply(line, nbpRates, indexes);
            postings.addAll(postingsFromLine);
        }
        return postings;
    }
}