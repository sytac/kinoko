package com.sytac.caseapocalypse.model;

public enum Stages {

    INIT("init"),
    DEVCASE_AVAILABLE_FOR_REVIEW("devcase_available_for_review"),
    REVIEW_COMPLETE("review_complete"),
    DEVCASE_ARCHIVED("devcase_archived");

    private String stage;

    Stages(String stage) {
        this.stage = stage;
    }

    public String get() {
        return stage;
    }
}
