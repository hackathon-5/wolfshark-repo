package us.hexcoder.polyticks.configuration;

import org.apache.commons.dbcp2.BasicDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.postgresql.Driver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;


/**
 * Created by 67726e on 8/28/15.
 */
@Configuration
@EnableTransactionManagement
public class PersistenceConfiguration {
	@Value("${database.url}")
	private String databaseUrl;
	@Value("${database.username}")
	private String databaseUsername;
	@Value("${database.password}")
	private String databasePassword;

	public TransactionAwareDataSourceProxy setupTransactionAwareDataSourceProxy() {
		return new TransactionAwareDataSourceProxy(setupDataSource());
	}

	@Bean
	public DataSource setupDataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriver(new Driver());
		dataSource.setUrl(databaseUrl);
		dataSource.setUsername(databaseUsername);
		dataSource.setPassword(databasePassword);

		return dataSource;
	}

	@Bean
	public PlatformTransactionManager setupDatabaseTransactionManager() {
		return new DataSourceTransactionManager(setupDataSource());
	}

	@Bean
	public DataSourceConnectionProvider setupDataSourceConnectionProvider() {
		return new DataSourceConnectionProvider(setupTransactionAwareDataSourceProxy());
	}

	@Bean
	public DSLContext setupDslContext() {
		return new DefaultDSLContext(setupDefaultConfiguration());
	}

	@Bean
	public DefaultConfiguration setupDefaultConfiguration() {
		DefaultConfiguration configuration = new DefaultConfiguration();
		configuration.setSQLDialect(SQLDialect.POSTGRES);
		configuration.setConnectionProvider(setupDataSourceConnectionProvider());

		return configuration;
	}
}
