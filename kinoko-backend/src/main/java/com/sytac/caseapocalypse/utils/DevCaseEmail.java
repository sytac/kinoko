package com.sytac.caseapocalypse.utils;

import com.sytac.caseapocalypse.model.db.Template;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class DevCaseEmail {

    private Map<String, Object> content = new HashMap();
    private Template template;

}
