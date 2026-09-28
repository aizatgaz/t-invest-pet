package org.invest.configurations;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;


@Configuration
@EnableTransactionManagement
@PropertySource("classpath:db.properties")
public class PostgresConfiguration {

    @Bean
    public DataSource dataSource(
            @Value("db.user") String user,
            @Value("db.password") String password,
            @Value("db.url") String url
    ) {
        var dataSource = new PGSimpleDataSource();
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setUrl(url);
        return dataSource;
    }

    @Bean
    public NamedParameterJdbcTemplate jdbcTemplate(
            DataSource dataSource
    ) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

}
