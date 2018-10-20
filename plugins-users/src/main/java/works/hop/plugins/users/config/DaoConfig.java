package works.hop.plugins.users.config;

import java.sql.Connection;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.sqlite.SQLiteConfig;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import works.hop.plugins.users.dao.UsersService;
import works.hop.plugins.users.dao.UsersServiceImpl;

@Configuration
@EnableTransactionManagement
public class DaoConfig {

	@Bean
	public DataSource dataSource() {
		// Create the ConnectionPoolDataSource
		SQLiteConnectionPoolDataSource ds = new SQLiteConnectionPoolDataSource();
		ds.setUrl("jdbc:sqlite:./data/plugins-users.db");

		// Pass in some additional config options (optional)
		SQLiteConfig config = new SQLiteConfig();
		config.enforceForeignKeys(true);
		config.enableLoadExtension(true);
		ds.setConfig(config);

		// initialize database if need be
		try {
			Connection conn = ds.getConnection();
			if (conn != null) {
				ResultSet rs = conn.getMetaData().getTables(null, null, "tbl_users", null);
				if (!rs.next()) {
					ResourceDatabasePopulator schema = new ResourceDatabasePopulator();
					schema.addScript(new FileSystemResource("data/sql/create-tables.sql"));
					schema.execute(ds);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// return datasource bean
		return ds;
	}

	@Bean
	public PlatformTransactionManager txManager() {
		return new DataSourceTransactionManager(dataSource());
	}

	@Bean
	public UsersService userService(@Autowired DataSource ds) {
		return new UsersServiceImpl(ds);
	}
}
