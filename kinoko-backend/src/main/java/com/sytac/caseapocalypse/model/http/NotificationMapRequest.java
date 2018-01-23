package com.sytac.caseapocalypse.model.http;

import lombok.Data;

@Data
public class NotificationMapRequest {
    private Long role;
    private Long stage;
    private Long template;
}
