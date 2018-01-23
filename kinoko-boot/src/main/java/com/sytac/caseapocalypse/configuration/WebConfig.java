package com.sytac.caseapocalypse.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

//@Configuration
public class WebConfig {

    /**
     * The endpoints authorized to connect to the servers (example the frontend)
     */
    //@Value("${cors.ip.frontend}")
    private String FRONTEND;

    //@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            //@Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(FRONTEND)
                        .allowedMethods("GET", "POST", "PUT", "OPTIONS", "DELETE");
            }
        };
    }
}
