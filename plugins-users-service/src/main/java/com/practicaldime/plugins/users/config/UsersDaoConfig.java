package com.practicaldime.plugins.users.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.sqlite.SQLiteConfig;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import javax.sql.DataSource;

@Configuration
public class UsersDaoConfig {

    @Bean
    public DataSource dataSource() {
        // Create the ConnectionPoolDataSource
        SQLiteConnectionPoolDataSource ds = new SQLiteConnectionPoolDataSource();
        ds.setUrl("jdbc:sqlite:./data/rest-tools.db");

        // Pass in some additional config options (optional)
        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);
        config.enableLoadExtension(true);
        ds.setConfig(config);

        // return datasource bean
        return ds;
    }

    @Bean
    public PlatformTransactionManager txManager() {
        return new DataSourceTransactionManager(dataSource());
    }
}
