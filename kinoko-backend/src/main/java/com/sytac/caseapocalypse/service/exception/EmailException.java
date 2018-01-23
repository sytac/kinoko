package com.sytac.caseapocalypse.service.exception;

public class EmailException extends Exception {

    public EmailException(String message) {
        super(message);
    }

    public EmailException(String message, Exception e) {
        super(message, e);
    }
}
