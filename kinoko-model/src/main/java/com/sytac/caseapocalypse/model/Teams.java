package com.sytac.caseapocalypse.model;

public enum Teams {

    FRONTEND_REVIEWERS("frontend-reviewers"), BACKEND_REVIEWERS("backend-reviewers"), ANDROID_REVIEWERS("android-reviewers");

    private String value;

    Teams(String value) {
        this.value = value;
    }

    public String get() {
        return value;
    }
}
