package com.practicaldime.plugins.todos;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TodoConfig {

    @Bean(name = "app")
    public TodoService getBasicApp() {
        return new TodoService();
    }
}
