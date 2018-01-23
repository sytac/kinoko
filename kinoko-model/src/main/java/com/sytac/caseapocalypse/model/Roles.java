package com.sytac.caseapocalypse.model;

public enum Roles {

    CANDIDATE("candidate"),
    REVIEWER("reviewer"),
    REVIEWERS("reviewers"),
    RECRUITER("recruiter"),
    ADMIN("admin"),
    CREATOR("creator"),
    GENERIC_USER("generic_user");

    private String role;

    Roles(String role) {
        this.role = role;
    }

    public String get() {
        return role;
    }
}
