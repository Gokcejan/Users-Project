package cz.acrobits.csp.app_users_management.configuration;

import com.zaxxer.hikari.HikariDataSource;
import cz.acrobits.csp.app_users_management.Application;
import cz.acrobits.csp.core.jpa.CspCoreJpaPackageDefiningType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

@Configuration
@EntityScan(basePackageClasses = {Application.class, CspCoreJpaPackageDefiningType.class})
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
@EnableJpaRepositories(basePackageClasses = {Application.class, CspCoreJpaPackageDefiningType.class})
@EnableTransactionManagement
public class DatabaseConfiguration {

    @Autowired
    private DataSourceProperties properties;

    @Value("${spring.datasource.hikari.maximum-pool-size:10}")
    private int poolSize;

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = DataSourceBuilder
                .create(this.properties.getClassLoader())
                .type(HikariDataSource.class)
                .driverClassName(this.properties.getDriverClassName())
                .url(this.properties.getUrl())
                .username(this.properties.getUsername())
                .password(this.properties.getPassword())
                .build();

        dataSource.setMaximumPoolSize(poolSize);

        TransactionAwareDataSourceProxy dataSourceProxy = new TransactionAwareDataSourceProxy(dataSource);

        return dataSourceProxy;
    }
}
