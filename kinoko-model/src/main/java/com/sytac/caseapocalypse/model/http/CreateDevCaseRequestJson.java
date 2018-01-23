package com.sytac.caseapocalypse.model.http;

import lombok.Data;

import java.util.Date;

@Data
public class CreateDevCaseRequestJson {

    private String type;
    private Date deadline;
    private String candidate_account;
    private String candidate_email;
    private String candidate_name;


}
