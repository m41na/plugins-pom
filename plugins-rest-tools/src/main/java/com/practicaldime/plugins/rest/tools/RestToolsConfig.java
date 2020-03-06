package com.practicaldime.plugins.rest.tools;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestToolsConfig {

    @Bean(name = "app")
    public RestToolsService getRestService() {
        return new RestToolsService();
    }
}
