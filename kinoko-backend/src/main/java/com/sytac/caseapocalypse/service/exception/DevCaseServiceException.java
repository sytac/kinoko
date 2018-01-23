package com.sytac.caseapocalypse.service.exception;

public class DevCaseServiceException extends Exception {

    public DevCaseServiceException(String message) {
        super(message);
    }

    public DevCaseServiceException(String message, Exception e) {
        super(message, e);
    }
}
