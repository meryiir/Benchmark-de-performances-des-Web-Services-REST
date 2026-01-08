package com.example.demo.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    private final Environment environment;

    public DataSourceConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(environment.getProperty("spring.datasource.url"));
        config.setUsername(environment.getProperty("spring.datasource.username"));
        
        // Read password directly from environment to preserve spaces
        // Check environment variable first, then fall back to property
        String password = System.getenv("DB_PASSWORD");
        if (password == null) {
            password = environment.getProperty("spring.datasource.password");
        }
        // If password is null or empty, use 4 spaces as per your configuration
        if (password == null || password.isEmpty()) {
            password = "    ";
        }
        config.setPassword(password);
        
        config.setMinimumIdle(environment.getProperty("spring.datasource.hikari.minimum-idle", Integer.class, 5));
        config.setMaximumPoolSize(environment.getProperty("spring.datasource.hikari.maximum-pool-size", Integer.class, 20));
        config.setConnectionTimeout(environment.getProperty("spring.datasource.hikari.connection-timeout", Long.class, 30000L));
        config.setIdleTimeout(environment.getProperty("spring.datasource.hikari.idle-timeout", Long.class, 600000L));
        config.setMaxLifetime(environment.getProperty("spring.datasource.hikari.max-lifetime", Long.class, 1800000L));
        return new HikariDataSource(config);
    }
}
