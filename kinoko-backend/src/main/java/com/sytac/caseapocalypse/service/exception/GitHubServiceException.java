package com.sytac.caseapocalypse.service.exception;

/**
 * Exception caught fo the GitHub actions
 */
public class GitHubServiceException extends Exception {

    public GitHubServiceException(String message) {
        super(message);
    }

    public GitHubServiceException(String message, Exception e) {
        super(message, e);
    }
}
