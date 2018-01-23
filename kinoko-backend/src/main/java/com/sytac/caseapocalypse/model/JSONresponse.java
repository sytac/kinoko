package com.sytac.caseapocalypse.model;

import lombok.Data;

@Data
public class JSONresponse {

    private String message;

    public JSONresponse(String message) {
        this.message = message;
    }
}
