package com.sytac.caseapocalypse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;


@SpringBootApplication
public class CaseApocalypseApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CaseApocalypseApplication.class);
    }

    public static void main(String[] args) {
        System.out.println("############### START ############");
        SpringApplication.run(CaseApocalypseApplication.class, args);
    }

}