package plugins.poc.users.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.sqlite.SQLiteConfig;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;
import plugins.poc.users.dao.UsersService;
import plugins.poc.users.dao.UsersServiceImpl;

import javax.sql.DataSource;
import java.io.File;

@Configuration
@EnableTransactionManagement
public class DaoTestConfig {

    @Bean
    public DataSource dataSource() {
        String dbPath = "./data/test-plugins-users.db";
        //drop database if exists
        File dbFile = new File(dbPath);
        if (dbFile.exists()) {
            String result = dbFile.delete() ? "database dropped" : "could not drop database";
            System.out.println(result);
        }

        // Create the ConnectionPoolDataSource
        SQLiteConnectionPoolDataSource ds = new SQLiteConnectionPoolDataSource();
        ds.setUrl("jdbc:sqlite:" + dbPath);

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

    @Bean
    @Autowired
    public UsersService UserService(DataSource ds) {
        return new UsersServiceImpl(ds);
    }
}
