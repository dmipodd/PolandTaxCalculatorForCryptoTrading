package com.dpod.plcryptotaxcalc.exception;

public class NbpRatesLoadingException extends RuntimeException {

    public NbpRatesLoadingException(Exception exception) {
        super(exception);
    }
}