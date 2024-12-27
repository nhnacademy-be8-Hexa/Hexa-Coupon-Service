package com.nhnacademy.coupon.config;

import com.nhnacademy.coupon.credentials.DatabaseCredentials;
import com.nhnacademy.coupon.service.credentials.SecureKeyManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

@Configuration
@Profile("prod")
public class DataSourceConfig {

    @Autowired
    private SecureKeyManagerService secureKeyManagerService;

    @Bean
    public DataSource dataSource(){

        String databaseInfo = secureKeyManagerService.fetchSecretFromKeyManager();
        DatabaseCredentials databaseCredentials = new DatabaseCredentials(databaseInfo);
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(databaseCredentials.getUrl());
        dataSource.setUsername(databaseCredentials.getUsername());
        dataSource.setPassword(databaseCredentials.getPassword());
        return dataSource;
    }
}
