package com.kindredgroup.hackday.gcs.ledger.exceptions;

/**
 * Created by Ashwini Bhalsingh on 2019-09-19
 */
public class InsufficientFundException extends RuntimeException {


    public InsufficientFundException(String s) {
        super(s);
    }
}
