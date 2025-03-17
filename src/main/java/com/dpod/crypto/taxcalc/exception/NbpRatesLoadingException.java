package com.dpod.crypto.taxcalc.exception;

public class NbpRatesLoadingException extends RuntimeException {

    public NbpRatesLoadingException(Exception exception) {
        super(exception);
    }
}