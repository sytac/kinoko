package com.sytac.caseapocalypse.model;

public enum ProjectTypes {

    FRONTEND("frontend"), BACKEND("backend"), ANDROID("android");

    private String value;

    ProjectTypes(String value) {
        this.value = value;
    }

    public String get() {
        return value;
    }
}
