package com.practicaldime.plugins.backlog.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {"com.practicaldime.plugins.backlog"})
public class BackLogServiceConfig {
}
