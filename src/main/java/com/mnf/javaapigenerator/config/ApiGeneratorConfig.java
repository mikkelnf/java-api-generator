package com.mnf.javaapigenerator.config;

import com.mnf.javaapigenerator.service.ApiGeneratorServiceImpl;
import com.mnf.javaapigenerator.service.IApiGeneratorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGeneratorConfig {
    @Bean
    public IApiGeneratorService apiGeneratorService(){
        return new ApiGeneratorServiceImpl();
    }
}
