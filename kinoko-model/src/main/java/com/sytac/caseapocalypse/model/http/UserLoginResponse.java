package com.sytac.caseapocalypse.model.http;

import lombok.Data;

@Data
public class UserLoginResponse {
    private String name;
    private String email;
    private String role;
}
