package com.dpod.crypto.taxcalc.process;

import com.dpod.crypto.taxcalc.nbp.NbpRates;
import com.dpod.crypto.taxcalc.posting.Posting;

import java.util.List;

public interface Processor {

    List<Posting> generatePostingsFor(NbpRates nbpRates, String filename);
}