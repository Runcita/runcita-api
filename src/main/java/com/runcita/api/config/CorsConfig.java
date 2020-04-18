package com.runcita.api.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
public class CorsConfig {

    @Bean
    public FilterRegistrationBean corsFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(corsFilter());
        registration.addUrlPatterns("/url/*");
        registration.addInitParameter("paramName", "paramValue");
        registration.setName("CORSFilter");
        registration.setOrder(1);
        return registration;
    }

    private Filter corsFilter() {
        return new CorsFilterConfig();
    }
}
