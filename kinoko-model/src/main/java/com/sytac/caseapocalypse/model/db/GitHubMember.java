package com.sytac.caseapocalypse.model.db;

import lombok.Data;

import java.io.Serializable;

@Data
public class GitHubMember implements Serializable {

    private String userName;
    private String email;


}
