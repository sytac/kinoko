package com.sytac.caseapocalypse.utils;

public class EmailUtilException extends Exception {

    public EmailUtilException(String message) {
        super(message);
    }

    public EmailUtilException(String message, Exception e) {
        super(message, e);
    }
}