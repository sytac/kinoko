package com.sytac.caseapocalypse.model;

public enum Permissions {

    PULL("pull"), PUSH("push"), ADMIN("admin");

    private String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    public String get() {
        return permission;
    }
}
